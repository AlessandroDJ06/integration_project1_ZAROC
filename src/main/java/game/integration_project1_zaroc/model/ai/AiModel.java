package game.integration_project1_zaroc.model.ai;

import ai.onnxruntime.*;
import game.integration_project1_zaroc.model.boardinfo.Pawn;
import game.integration_project1_zaroc.model.boardinfo.Peg;
import game.integration_project1_zaroc.model.gameinfo.PawnColor;
import game.integration_project1_zaroc.model.gamelogic.*;
import game.integration_project1_zaroc.model.players.AIPlayer;

import java.nio.FloatBuffer;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Core AI engine for Zaroc. Uses MCTS + optional ONNX neural network (AlphaZero style).
 *
 * Decision pipeline per turn (in order of priority):
 *   1. Instant win
 *   2. Block opponent instant win
 *   3. Score own pawn (reach row 3)
 *   4. Sabotage opponent on row 2 (only when no scoring move available)
 *   5. Repetition filter (avoid loops)
 *   6. Beam search ranked by net/heuristic blend
 *   7. Parallel MCTS for final decision
 *
 * Repetition fix: tracks the last 4 played turn-keys per AI instance.
 * Any turn that would repeat a recent position is penalized in ranking
 * and filtered from beam candidates when alternatives exist.
 *
 * @author Alessandro De Jongh
 * @version 6.0
 */
public class AiModel {

    // =========================================================================
    // Constants
    // =========================================================================

    private static final int[] MAX_STACK_PER_ROW  = {4, 3, 2, 1};
    private static final int   REPETITION_HISTORY  = 4;
    private static final int   BEAM_WIDTH          = 40;

    // =========================================================================
    // MCTS Parameters
    // =========================================================================

    private final int    totalIterations;
    private final int    numThreads       = Runtime.getRuntime().availableProcessors();
    private final double ucbConstant      = Math.sqrt(2);
    private final int    maxRolloutMoves  = 200;
    private final double heuristicBlend;  // 0.0 = pure net, 1.0 = pure heuristic

    // =========================================================================
    // Heuristic Weights
    // =========================================================================

    private int weightProgressStep    = 1500;
    private int weightAdvanceRow2     = 500;
    private int weightSabotageDrag    = 4000;
    private int weightControlOpponent = 6000;
    private int weightBlockThreshold  = 90000;
    private int penaltyOpponentFinish = -20000;
    private int scoreWin              = 100000;

    // =========================================================================
    // Identity
    // =========================================================================

    private PawnColor aiColor;
    private PawnColor opponentColor;
    private String    aiUsername;

    // =========================================================================
    // Repetition History
    // =========================================================================

    private final Deque<String> recentTurnKeys = new ArrayDeque<>();

    // =========================================================================
    // ONNX
    // =========================================================================

    private OrtEnvironment onnxEnv;
    private OrtSession     onnxSession;
    private boolean        netAvailable = false;

    private enum Result { PLAYING, AI_WINS, OPP_WINS }

    // =========================================================================
    // Constructor
    // =========================================================================

    public AiModel(int difficulty, String name) {
        this.aiUsername = name.toUpperCase();

        this.totalIterations = switch (difficulty) {
            case 0  -> 500;
            case 1  -> 1000;
            case 2  -> 2000;
            case 3  -> 5000;
            default -> 1000;
        };

        this.heuristicBlend = switch (difficulty) {
            case 0  -> 0.80;
            case 1  -> 0.60;
            case 2  -> 0.50;
            case 3  -> 0.45;
            default -> 0.60;
        };

        applyPersonality(aiUsername);
        loadNeuralNetwork();
    }

    // =========================================================================
    // Neural Network
    // =========================================================================

    private void loadNeuralNetwork() {
        try {
            String path = "/game/integration_project1_zaroc/neuralnetwork/zaroc_model.onnx";
            try (java.io.InputStream is = getClass().getResourceAsStream(path)) {
                if (is == null) throw new RuntimeException("Model not found: " + path);
                byte[] bytes = is.readAllBytes();
                onnxEnv = OrtEnvironment.getEnvironment();
                OrtSession.SessionOptions opts = new OrtSession.SessionOptions();
                opts.setIntraOpNumThreads(1);
                onnxSession = onnxEnv.createSession(bytes, opts);
                netAvailable = true;
            }
        } catch (Exception e) {
            netAvailable = false;
        }
    }

    /**
     * Encodes the board as a float[] matching HeadlessArena.extractBoardState() exactly.
     * Per peg: [slot0, slot1, slot2, slot3, isFull] then a turn indicator at the end.
     */
    private float[] encodeBoard(Game game) {
        Peg[][] allPegs = game.getBoard().getAllPegs();
        PawnColor p1Color = game.getParticipation1().getChosenPawnColor();

        int totalPegs = 0;
        for (Peg[] row : allPegs) totalPegs += row.length;

        float[] f = new float[totalPegs * 5 + 1];
        int idx = 0;

        for (int row = 0; row < allPegs.length; row++) {
            for (int col = 0; col < allPegs[row].length; col++) {
                Peg peg = allPegs[row][col];
                int maxStack = MAX_STACK_PER_ROW[row];
                int size = 0;
                float[] slots = new float[4];

                if (peg != null && !peg.getPawns().isEmpty()) {
                    List<Pawn> pawns = peg.getPawns();
                    size = pawns.size();
                    for (int s = 0; s < pawns.size() && s < 4; s++) {
                        slots[s] = pawns.get(s).getPawnColor().equals(p1Color) ? 1f : -1f;
                    }
                }

                for (int s = 0; s < 4; s++) f[idx++] = slots[s];
                f[idx++] = (size >= maxStack) ? 1f : 0f;
            }
        }

        boolean isP1Turn = game.getCurrentTurn().getCurrentPlayer().getUsername()
                .equals(game.getParticipation1().getPlayer().getUsername());
        f[idx] = isP1Turn ? 1f : -1f;
        return f;
    }

    /**
     * Runs the ONNX model. Returns AI win probability [0.01, 0.99], or -1 on failure.
     */
    private double runNet(Game game) {
        if (!netAvailable || onnxSession == null) return -1;
        try {
            float[] input = encodeBoard(game);
            OnnxTensor tensor = OnnxTensor.createTensor(
                    onnxEnv, FloatBuffer.wrap(input), new long[]{1, input.length});
            Map<String, OnnxTensor> inputs = new HashMap<>();
            inputs.put(onnxSession.getInputNames().iterator().next(), tensor);

            float[] output = (float[]) onnxSession.run(inputs).get(0).getValue();

            // Trained with labels -1/0/1 → output in [-1,1] → normalize to [0,1]
            double prob = (output[0] + 1.0) / 2.0;

            // Flip perspective if it's the opponent's turn
            boolean isAiTurn = game.getCurrentTurn().getCurrentPlayer()
                    .getUsername().trim().equalsIgnoreCase(aiUsername);
            double aiProb = isAiTurn ? prob : (1.0 - prob);

            return Math.max(0.01, Math.min(0.99, aiProb));
        } catch (Exception e) {
            return -1;
        }
    }

    // =========================================================================
    // Main Entry Point
    // =========================================================================

    public Turn getBestTurn(Game actualGame, AIPlayer aiPlayer) {
        setupIdentity(actualGame, aiPlayer);

        List<Turn> options = MoveGenerator.getAllLegalTurns(actualGame);
        if (options.isEmpty()) return null;

        // 1. Instant win
        for (Turn t : options) {
            if (applyAndCheck(actualGame, t) == Result.AI_WINS) return t;
        }

        // 2. Block opponent instant win
        if (countFinished(actualGame, opponentColor) >= 2) {
            List<Turn> safe = new ArrayList<>();
            for (Turn t : options) {
                Game copy = actualGame.gameCopy();
                applyTurn(copy, t);
                if (!opponentCanWinNext(copy)) safe.add(t);
            }
            if (!safe.isEmpty() && safe.size() < options.size()) options = safe;
        }

        // 3. Prefer scoring turns (own pawn reaches row 3)
        int myScore = countFinished(actualGame, aiColor);
        List<Turn> scoring = new ArrayList<>();
        for (Turn t : options) {
            Game copy = actualGame.gameCopy();
            applyTurn(copy, t);
            if (countFinished(copy, aiColor) > myScore) scoring.add(t);
        }
        boolean hasScoring = !scoring.isEmpty();
        if (hasScoring) {
            options = scoring;
            if (options.size() == 1) {
                recordTurn(options.get(0));
                return options.get(0);
            }
        }

        // 4. Sabotage row 2 — only when no scoring move exists
        if (!hasScoring) {
            int oppRow2 = countOnRow(actualGame, opponentColor, 2);
            if (oppRow2 > 0) {
                List<Turn> sabotage = new ArrayList<>();
                for (Turn t : options) {
                    Game copy = actualGame.gameCopy();
                    applyTurn(copy, t);
                    if (countOnRow(copy, opponentColor, 2) < oppRow2) sabotage.add(t);
                }
                if (!sabotage.isEmpty() && sabotage.size() < options.size()) options = sabotage;
            }
        }

        // 5. Repetition filter — remove recently played turns if alternatives exist
        List<Turn> nonRepeating = new ArrayList<>();
        for (Turn t : options) {
            if (!recentTurnKeys.contains(getTurnKey(t))) nonRepeating.add(t);
        }
        if (!nonRepeating.isEmpty() && nonRepeating.size() < options.size()) {
            options = nonRepeating;
        }

        // 6. Beam: sort by net/heuristic blend, penalizing repetitions
        options.sort((a, b) -> Double.compare(scoreTurn(b, actualGame), scoreTurn(a, actualGame)));

        // Skip MCTS if already very confident
        Turn topTurn = options.get(0);
        if (scoreTurn(topTurn, actualGame) >= 0.85) {
            recordTurn(topTurn);
            return topTurn;
        }

        List<Turn> beam = new ArrayList<>(options.subList(0, Math.min(options.size(), BEAM_WIDTH)));

        // 7. Parallel MCTS
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Callable<ZarocNode>> tasks = new ArrayList<>();
        int itersPerThread = Math.max(1, totalIterations / numThreads);
        for (int i = 0; i < numThreads; i++) {
            tasks.add(() -> runMctsThread(actualGame, itersPerThread));
        }

        List<ZarocNode> roots = new ArrayList<>();
        try {
            for (Future<ZarocNode> f : executor.invokeAll(tasks)) roots.add(f.get());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        Turn best = pickBestFromMcts(roots, beam);
        recordTurn(best);
        return best;
    }

    // =========================================================================
    // Repetition Tracking
    // =========================================================================

    private void recordTurn(Turn t) {
        if (t == null) return;
        if (recentTurnKeys.size() >= REPETITION_HISTORY) recentTurnKeys.pollFirst();
        recentTurnKeys.addLast(getTurnKey(t));
    }

    // =========================================================================
    // Turn Scoring
    // =========================================================================

    /**
     * Scores a turn: blends net evaluation with heuristic, and applies a repetition penalty.
     */
    private double scoreTurn(Turn t, Game game) {
        Game copy = game.gameCopy();
        applyTurn(copy, t);
        copy.switchCurrentPlayer();

        double netScore = runNet(copy);
        double hScore   = normalizeHeuristic(heuristicTurnScore(t, game));

        double base;
        if (netScore < 0 || heuristicBlend >= 1.0) {
            base = hScore;
        } else if (heuristicBlend <= 0.0) {
            base = netScore;
        } else {
            base = (1.0 - heuristicBlend) * netScore + heuristicBlend * hScore;
        }

        // Strongly penalize any turn we've played recently
        if (recentTurnKeys.contains(getTurnKey(t))) base *= 0.3;

        return base;
    }

    private double normalizeHeuristic(int raw) {
        return Math.max(0.0, Math.min(1.0, 0.5 + raw / 2_000_000.0));
    }

    // =========================================================================
    // Heuristic Evaluation
    // =========================================================================

    private int heuristicTurnScore(Turn t, Game g) {
        if (t == null || t.getFirstMove() == null) return -99999;
        int s = heuristicMoveScore(t.getFirstMove(), g);
        if (t.getSecondMove() != null) s += heuristicMoveScore(t.getSecondMove(), g);
        return s;
    }

    private int heuristicMoveScore(Move m, Game g) {
        if (m == null || m.getStartPeg() == null) return 0;

        Peg dest  = m.getDestinationPeg();
        Peg start = m.getStartPeg();
        int row   = dest.getYPosition();
        double tb = threatBias(g);

        if (row == 3) return scoreWin * 10;

        int s = (row == 2) ? weightAdvanceRow2 : 0;

        if (row > start.getYPosition())      s += weightProgressStep;
        else if (row < start.getYPosition()) s -= weightProgressStep;

        // Bonus for dragging captured opponent pawns further up
        int hostages = 0;
        for (Pawn p : start.getPawns()) {
            if (p.getPawnColor().equals(opponentColor)) hostages++;
        }
        if (hostages > 0) s += (int)(weightSabotageDrag * hostages * (row + 1));

        // Bonus for landing on opponent (capture / stack takeover)
        Peg realDest = g.getBoard().getPegPosition(dest.getYPosition(), dest.getXPosition());
        if (realDest != null && !realDest.getPawns().isEmpty()
                && realDest.getUpperPawn().getPawnColor().equals(opponentColor)) {
            double mFactor = (row == 2) ? (tb * 4) : tb;
            s += (int)(weightControlOpponent * mFactor);
            if (row == 2) s += (int)(weightBlockThreshold * tb);
        }

        return s;
    }

    private Turn heuristicBestTurn(List<Turn> opts, Game g) {
        return opts.stream()
                .max(Comparator.comparingInt(t -> heuristicTurnScore(t, g)))
                .orElse(opts.get(0));
    }

    // =========================================================================
    // MCTS
    // =========================================================================

    private ZarocNode runMctsThread(Game base, int iterations) {
        ZarocNode root = new ZarocNode(base.gameCopy(), null, null);
        Random rnd = new Random();
        for (int i = 0; i < iterations; i++) {
            ZarocNode leaf = select(root);
            ZarocNode node = expand(leaf, rnd);
            backpropagate(node, rollout(node, rnd));
        }
        return root;
    }

    private ZarocNode select(ZarocNode node) {
        while (!node.getChildren().isEmpty()) {
            node = node.getChildren().stream()
                    .max(Comparator.comparingDouble(c -> c.getUCBValue(ucbConstant)))
                    .get();
        }
        return node;
    }

    private ZarocNode expand(ZarocNode node, Random rnd) {
        if (checkResult(node.getState()) != Result.PLAYING) return node;
        List<Turn> opts = MoveGenerator.getAllLegalTurns(node.getState());
        Collections.shuffle(opts, rnd);
        for (Turn t : opts) {
            String key = getTurnKey(t);
            boolean alreadyExpanded = node.getChildren().stream()
                    .anyMatch(c -> getTurnKey(c.getInboundTurn()).equals(key));
            if (!alreadyExpanded) {
                Game next = node.getState().gameCopy();
                applyTurn(next, t);
                next.switchCurrentPlayer();
                ZarocNode child = new ZarocNode(next, node, t);
                node.addChild(child);
                return child;
            }
        }
        return node;
    }

    private double rollout(ZarocNode node, Random rnd) {
        Game sim = node.getState().gameCopy();

        Result res = checkResult(sim);
        if (res == Result.AI_WINS)  return 1.0;
        if (res == Result.OPP_WINS) return 0.0;

        double netVal = runNet(sim);
        if (netVal >= 0 && heuristicBlend < 1.0) {
            if (heuristicBlend <= 0.0) return netVal;
            double rolloutVal = shortRollout(sim, rnd, 5);
            return (1.0 - heuristicBlend) * netVal + heuristicBlend * rolloutVal;
        }

        return fullRollout(sim, rnd);
    }

    private double shortRollout(Game sim, Random rnd, int depth) {
        for (int i = 0; i < depth; i++) {
            Result res = checkResult(sim);
            if (res == Result.AI_WINS)  return 1.0;
            if (res == Result.OPP_WINS) return 0.0;
            List<Turn> opts = MoveGenerator.getAllLegalTurns(sim);
            if (opts.isEmpty()) break;
            applyTurn(sim, heuristicBestTurn(opts, sim));
            sim.switchCurrentPlayer();
        }
        return progressScore(sim);
    }

    private double fullRollout(Game sim, Random rnd) {
        for (int i = 0; i < maxRolloutMoves; i++) {
            Result res = checkResult(sim);
            if (res == Result.AI_WINS)  return 1.0;
            if (res == Result.OPP_WINS) return 0.0;
            List<Turn> opts = MoveGenerator.getAllLegalTurns(sim);
            if (opts.isEmpty()) break;
            Turn t = (rnd.nextDouble() < 0.65)
                    ? heuristicBestTurn(opts, sim)
                    : opts.get(rnd.nextInt(opts.size()));
            applyTurn(sim, t);
            sim.switchCurrentPlayer();
        }
        return progressScore(sim);
    }

    private void backpropagate(ZarocNode node, double score) {
        ZarocNode curr = node;
        while (curr != null) {
            curr.addVisit();
            if (curr.getInboundTurn() != null) {
                boolean isAi = curr.getInboundTurn().getCurrentPlayer()
                        .getUsername().trim().equalsIgnoreCase(aiUsername);
                curr.addScore(isAi ? score : (1.0 - score));
            }
            curr = curr.getParent();
        }
    }

    private Turn pickBestFromMcts(List<ZarocNode> roots, List<Turn> beam) {
        Map<String, MoveStats> data = new HashMap<>();
        for (ZarocNode root : roots) {
            for (ZarocNode child : root.getChildren()) {
                String key = getTurnKey(child.getInboundTurn());
                if (beam.stream().noneMatch(o -> getTurnKey(o).equals(key))) continue;
                data.putIfAbsent(key, new MoveStats());
                MoveStats s = data.get(key);
                s.turn = child.getInboundTurn();
                s.visits     += child.getVisits();
                s.totalScore += child.getScore();
            }
        }
        if (data.isEmpty()) return beam.get(0);
        return data.values().stream()
                .max(Comparator.comparingDouble(s -> s.totalScore / s.visits))
                .get().turn;
    }

    // =========================================================================
    // Game State Helpers
    // =========================================================================

    private Result applyAndCheck(Game game, Turn t) {
        Game copy = game.gameCopy();
        applyTurn(copy, t);
        return checkResult(copy);
    }

    private Result checkResult(Game game) {
        int ai  = countFinished(game, aiColor);
        int opp = countFinished(game, opponentColor);
        if (opp >= 3) return Result.OPP_WINS;
        if (ai  >= 3) return Result.AI_WINS;
        return Result.PLAYING;
    }

    private boolean opponentCanWinNext(Game game) {
        game.switchCurrentPlayer();
        boolean canWin = MoveGenerator.getAllLegalTurns(game).stream().anyMatch(t -> {
            Game copy = game.gameCopy();
            applyTurn(copy, t);
            return checkResult(copy) == Result.OPP_WINS;
        });
        game.switchCurrentPlayer();
        return canWin;
    }

    private int countFinished(Game game, PawnColor color) {
        int count = 0;
        for (Peg[] row : game.getBoard().getAllPegs()) {
            for (Peg p : row) {
                if (p != null && p.getYPosition() == 3 && !p.getPawns().isEmpty()
                        && p.getUpperPawn().getPawnColor().equals(color)) count++;
            }
        }
        return count;
    }

    private int countOnRow(Game game, PawnColor color, int targetRow) {
        int count = 0;
        for (Peg p : game.getBoard().getAllPegs()[targetRow]) {
            if (p != null && !p.getPawns().isEmpty()
                    && p.getUpperPawn().getPawnColor().equals(color)) count++;
        }
        return count;
    }

    private double threatBias(Game game) {
        int oppFin    = countFinished(game, opponentColor);
        int oppOnRow2 = countOnRow(game, opponentColor, 2);
        return Math.min(30.0, 1.0 + (oppFin * 6.0) + (oppOnRow2 * 4.0));
    }

    private double progressScore(Game game) {
        double aiScore = 0, oppScore = 0;
        int oppFin = countFinished(game, opponentColor);
        double tb  = threatBias(game);

        for (Peg[] row : game.getBoard().getAllPegs()) {
            for (Peg p : row) {
                if (p == null || p.getPawns().isEmpty()) continue;
                double val = switch (p.getYPosition()) {
                    case 3  -> 5_000_000.0;
                    case 2  -> (oppFin >= 2) ? 400_000.0 : 50_000.0;
                    case 1  -> 5_000.0;
                    default -> 100.0;
                };
                if (p.getUpperPawn().getPawnColor().equals(aiColor)) {
                    aiScore += val;
                    for (Pawn h : p.getPawns()) {
                        if (h.getPawnColor().equals(opponentColor)) aiScore += val * 0.25;
                    }
                } else {
                    double m = (p.getYPosition() == 2 && oppFin >= 2) ? (tb * 4) : 1.0;
                    oppScore += val * m;
                }
            }
        }
        if (oppFin >= 3) return 0.0;
        return Math.max(0.01, Math.min(0.99, 0.5 + (aiScore - oppScore) / 20_000_000.0));
    }

    private void setupIdentity(Game game, AIPlayer aiPlayer) {
        this.aiUsername = aiPlayer.getUsername().trim().toUpperCase();
        boolean p1IsAi = game.getParticipation1().getPlayer().getUsername()
                .trim().equalsIgnoreCase(aiUsername);
        aiColor       = p1IsAi ? game.getParticipation1().getChosenPawnColor()
                : game.getParticipation2().getChosenPawnColor();
        opponentColor = p1IsAi ? game.getParticipation2().getChosenPawnColor()
                : game.getParticipation1().getChosenPawnColor();
    }

    private void applyTurn(Game game, Turn turn) {
        if (turn.getFirstMove()  != null) applyMove(game, turn.getFirstMove());
        if (turn.getSecondMove() != null) applyMove(game, turn.getSecondMove());
    }

    private void applyMove(Game game, Move m) {
        Peg start = game.getBoard().getPegPosition(
                m.getStartPeg().getYPosition(), m.getStartPeg().getXPosition());
        Peg dest  = game.getBoard().getPegPosition(
                m.getDestinationPeg().getYPosition(), m.getDestinationPeg().getXPosition());
        if (start != null && dest != null && !start.getPawns().isEmpty()) {
            game.selectStartPeg(start);
            game.executeMove(dest);
        }
    }

    private String getTurnKey(Turn t) {
        if (t == null || t.getFirstMove() == null || t.getFirstMove().getStartPeg() == null)
            return "invalid_" + System.nanoTime();
        Move m1 = t.getFirstMove();
        String k = m1.getStartPeg().getXPosition() + "," + m1.getStartPeg().getYPosition()
                + ">" + m1.getDestinationPeg().getXPosition() + "," + m1.getDestinationPeg().getYPosition();
        if (t.getSecondMove() != null && t.getSecondMove().getStartPeg() != null) {
            Move m2 = t.getSecondMove();
            k += "|" + m2.getStartPeg().getXPosition() + "," + m2.getStartPeg().getYPosition()
                    + ">" + m2.getDestinationPeg().getXPosition() + "," + m2.getDestinationPeg().getYPosition();
        }
        return k;
    }

    // =========================================================================
    // Personality Profiles
    // =========================================================================

    private void applyPersonality(String name) {
        switch (name) {
            case "ARTHUR"    -> { weightProgressStep = 800;      weightControlOpponent = 3000; }
            case "ALISTAIR"  -> { weightBlockThreshold = 120000; weightProgressStep = 1000; }
            case "BEATRICE"  -> { penaltyOpponentFinish = -40000; weightAdvanceRow2 = 200; }
            case "CLARA"     -> { weightProgressStep = 1800; }
            case "ELEANOR"   -> { weightAdvanceRow2 = 1200; }
            case "GIDEON"    -> { weightControlOpponent = 9000;  weightProgressStep = 2000; }
            case "HELENA"    -> { weightBlockThreshold = 200000; }
            case "IRENE"     -> { weightSabotageDrag = 6000; }
            case "JAMES"     -> { weightBlockThreshold = 150000; weightSabotageDrag = 8000; }
            case "LEOPOLD"   -> { weightProgressStep = 3000;     weightAdvanceRow2 = 2000; }
            case "SEBASTIAN" -> { weightSabotageDrag = 15000;    weightControlOpponent = 12000; }
            case "STEFAN"    -> { weightBlockThreshold = 180000; weightProgressStep = 2500; weightSabotageDrag = 10000; }
        }
    }

    // =========================================================================
    // Inner Classes
    // =========================================================================

    private static class MoveStats {
        Turn   turn;
        int    visits;
        double totalScore;
    }
}