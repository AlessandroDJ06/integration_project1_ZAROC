package game.integration_project1_zaroc.model.ai;

import game.integration_project1_zaroc.model.gameinfo.PawnColor;
import game.integration_project1_zaroc.model.gamelogic.*;
import game.integration_project1_zaroc.model.boardinfo.Peg;
import game.integration_project1_zaroc.model.boardinfo.Pawn;
import game.integration_project1_zaroc.model.players.AIPlayer;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The core AI logic engine for the Zaroc game, featuring <b>12 unique personalities</b>.
 * <p>
 * This model utilizes a hybrid approach: <b>Monte Carlo Tree Search (MCTS)</b> for strategic depth,
 * <b>Minimax</b> for tactical safety, and dynamic heuristic weighting to simulate diverse playstyles.
 *
 * @author Alessandro De Jongh
 * @version 4.0
 * @see MoveGenerator
 * @see ZarocNode
 */
public class AiModel {
    private final int totalIterations;
    private final int maxMoves = 250;
    private final double ucbConstantValue = Math.sqrt(2);
    private final int numThreads = Runtime.getRuntime().availableProcessors();
    private final int minimaxDepth;

    // Dynamic Heuristic Weightings (Adjusted per personality)
    private int scoreWin = 100000;
    private int weightAdvanceRow2 = 500;
    private int weightControlOpponent = 6000;
    private int weightBlockThreshold = 90000;
    private int penaltyOpponentNearFinish = -20000;
    private int weightSabotageDrag = 4000;
    private int weightProgressStep = 1500;

    private PawnColor aiColor;
    private PawnColor opponentColor;
    private String aiUsername;

    /**
     * Enumeration for internal game state simulation results.
     */
    private enum InternalResult { PLAYING, AI_WINS, OPP_WINS }

    /**
     * Constructs the AI model with parameters based on the UI difficulty row and character name.
     *
     * @param difficulty The skill category (0: Easy, 1: Medium, 2: Hard, 3: Elite).
     * @param name       The username of the AI player used to determine personality weights.
     */
    public AiModel(int difficulty, String name) {
        this.aiUsername = name.toUpperCase();

        // Denkkracht instellen op basis van rij-index in de UI
        this.totalIterations = switch (difficulty) {
            case 0 -> 25000;   // Easy
            case 1 -> 100000;  // Medium
            case 2 -> 400000;  // Hard
            case 3 -> 800000;  // Elite
            default -> 100000;
        };

        // Tactische vooruitblik diepte
        this.minimaxDepth = switch (difficulty) {
            case 0 -> 2;
            case 1 -> 3;
            case 2 -> 4;
            case 3 -> 5;
            default -> 3;
        };

        applyPersonalityWeights(aiUsername);
    }

    /**
     * Maps the character name to specific heuristic weights to create a unique playstyle.
     *
     * @param name The character name in uppercase.
     */
    private void applyPersonalityWeights(String name) {
        switch (name) {
            // EASY
            case "ARTHUR" -> {
                this.weightProgressStep = 800;
                this.weightControlOpponent = 3000;
            }
            case "ALISTAIR" -> {
                this.weightBlockThreshold = 120000;
                this.weightProgressStep = 1000;
            }
            case "BEATRICE" -> {
                this.penaltyOpponentNearFinish = -40000;
                this.weightAdvanceRow2 = 200;
            }
            // MEDIUM
            case "CLARA" -> { this.weightProgressStep = 1800; }
            case "ELEANOR" -> { this.weightAdvanceRow2 = 1200; }
            case "GIDEON" -> {
                this.weightControlOpponent = 9000;
                this.weightProgressStep = 2000;
            }
            // HARD
            case "HELENA" -> { this.weightBlockThreshold = 200000; }
            case "IRENE" -> { this.weightSabotageDrag = 6000; }
            case "JAMES" -> {
                this.weightBlockThreshold = 150000;
                this.weightSabotageDrag = 8000;
            }
            // ELITE
            case "LEOPOLD" -> {
                this.weightProgressStep = 3000;
                this.weightAdvanceRow2 = 2000;
            }
            case "SEBASTIAN" -> {
                this.weightSabotageDrag = 15000;
                this.weightControlOpponent = 12000;
            }
            case "STEFAN" -> {
                this.weightBlockThreshold = 180000;
                this.weightProgressStep = 2500;
                this.weightSabotageDrag = 10000;
            }
        }
    }

    /**
     * Determines the optimal turn by passing all legal moves through a layered logic system.
     *
     * @param actualGame The current live {@link Game} state.
     * @param aiPlayer   The {@link AIPlayer} requesting the turn.
     * @return The identified best {@link Turn}.
     */
    public Turn getBestTurn(Game actualGame, AIPlayer aiPlayer) {
        setupAiIdentity(actualGame, aiPlayer);
        List<Turn> options = MoveGenerator.getAllLegalTurns(actualGame);
        if (options.isEmpty()) return null;

        // [1] Win detection
        for (Turn t : options) {
            Game testGame = actualGame.gameCopy();
            applyTurn(testGame, t);
            if (checkWinInternal(testGame) == InternalResult.AI_WINS) {
                System.out.println("AI LOG: [" + aiUsername + "] - Victory path identified. Executing.");
                return t;
            }
        }

        // [2] Defensive emergency
        if (countFinishedPawns(actualGame, opponentColor) >= 2) {
            List<Turn> survivalOptions = new ArrayList<>();
            for (Turn t : options) {
                Game testGame = actualGame.gameCopy();
                applyTurn(testGame, t);
                if (!canOpponentWinImmediately(testGame)) survivalOptions.add(t);
            }
            if (!survivalOptions.isEmpty() && survivalOptions.size() < options.size()) {
                System.out.println("AI LOG: [" + aiUsername + "] - Defensive override active.");
                options = survivalOptions;
            }
        }

        // [3] Minimax tactical filter
        List<Turn> safeTurns = new ArrayList<>();
        for (Turn t : options) {
            Game testGame = actualGame.gameCopy();
            applyTurn(testGame, t);
            if (!isForcedLoss(testGame, minimaxDepth, false)) safeTurns.add(t);
        }

        if (!safeTurns.isEmpty() && safeTurns.size() < options.size()) {
            System.out.println("AI LOG: [" + aiUsername + "] - Pruned " + (options.size() - safeTurns.size()) + " blunders.");
            options = safeTurns;
        } else if (safeTurns.isEmpty()) {
            System.out.println("AI LOG: [" + aiUsername + "] - Stalemate/Survival mode initiated.");
            options.sort((a, b) -> Integer.compare(getTurnScore(b, true, actualGame), getTurnScore(a, true, actualGame)));
            return options.get(0);
        }

        // [4] Scoring drive check
        int currentScore = countFinishedPawns(actualGame, aiColor);
        List<Turn> scoringTurns = new ArrayList<>();
        for (Turn t : options) {
            Game testGame = actualGame.gameCopy();
            applyTurn(testGame, t);
            if (countFinishedPawns(testGame, aiColor) > currentScore) scoringTurns.add(t);
        }
        if (!scoringTurns.isEmpty()) {
            options = scoringTurns;
            if (options.size() == 1) return options.get(0);
        }

        // [5] Strategic MCTS Simulation
        options.sort((a, b) -> Integer.compare(getTurnScore(b, true, actualGame), getTurnScore(a, true, actualGame)));
        int beamWidth = Math.min(options.size(), 40);
        options = new ArrayList<>(options.subList(0, beamWidth));

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Callable<ZarocNode>> tasks = new ArrayList<>();
        int iterationsPerThread = totalIterations / numThreads;
        for (int i = 0; i < numThreads; i++) tasks.add(() -> runMctsThread(actualGame, iterationsPerThread));

        List<ZarocNode> rootNodes = new ArrayList<>();
        try {
            List<Future<ZarocNode>> futures = executor.invokeAll(tasks);
            for (Future<ZarocNode> f : futures) rootNodes.add(f.get());
        } catch (Exception e) { e.printStackTrace(); } finally { executor.shutdown(); }

        Turn finalTurn = getBestCombinedTurn(rootNodes, options, actualGame);
        System.out.println("AI LOG: [" + aiUsername + "] - Strategy finalized via MCTS.");
        return finalTurn;
    }

    /**
     * Evaluates a single move based on progression, control, and personality-driven weights.
     *
     * @param m    The {@link Move} to evaluate.
     * @param isAi Perspectives of scoring (True for AI).
     * @param g    The game context for threat assessment.
     * @return An integer representing the move's heuristic value.
     */
    private int evaluateSingleMove(Move m, boolean isAi, Game g) {
        if (m == null || m.getStartPeg() == null) return 0;
        Peg dest = m.getDestinationPeg();
        Peg start = m.getStartPeg();
        int row = dest.getYPosition();
        double tb = calculateThreatBias(g);

        if (isAi) {
            int s = (row == 3) ? scoreWin * 10 : ((row == 2) ? weightAdvanceRow2 : 0);
            if (row > start.getYPosition()) s += weightProgressStep;
            else if (row < start.getYPosition()) s -= weightProgressStep;

            int hostages = 0;
            for (Pawn p : start.getPawns()) {
                if (p.getPawnColor().equals(opponentColor)) hostages++;
            }
            if (hostages > 0) s += (int) (weightSabotageDrag * hostages * (row + 1));

            if (!dest.getPawns().isEmpty() && dest.getUpperPawn().getPawnColor().equals(opponentColor)) {
                double mFactor = (row == 2) ? (tb * 4) : tb;
                s += (int) (weightControlOpponent * mFactor);
                if (row == 2) s += (int) (weightBlockThreshold * tb);
            }
            return s;
        }
        return (row == 3) ? (int) (-scoreWin * 15 * tb) : ((row == 2) ? (int) (penaltyOpponentNearFinish * tb) : 0);
    }

    /**
     * Values a board state for simulations using control metrics.
     *
     * @param game The game state to evaluate.
     * @return A normalized win probability (0.0 to 1.0).
     */
    private double calculateProgressForAI(Game game) {
        double aiScore = 0, oppScore = 0;
        int oppFin = countFinishedPawns(game, opponentColor);
        double threatBias = calculateThreatBias(game);

        for (Peg[] row : game.getBoard().getAllPegs()) {
            for (Peg p : row) {
                if (p == null || p.getPawns().isEmpty()) continue;
                double val = switch (p.getYPosition()) {
                    case 3 -> 5000000.0;
                    case 2 -> (oppFin >= 2) ? 400000.0 : 50000.0;
                    case 1 -> 5000.0;
                    default -> 100.0;
                };

                Pawn owner = p.getUpperPawn();
                if (owner.getPawnColor().equals(aiColor)) {
                    aiScore += val;
                    for (Pawn hostage : p.getPawns()) {
                        if (hostage.getPawnColor().equals(opponentColor)) aiScore += (val * 0.25);
                    }
                } else {
                    double m = (p.getYPosition() == 2 && oppFin >= 2) ? (threatBias * 4) : 1.0;
                    oppScore += (val * m);
                }
            }
        }
        return (oppFin >= 3) ? 0.0 : Math.max(0.01, Math.min(0.99, 0.5 + (aiScore - oppScore) / 20000000.0));
    }

    /**
     * Identifies AI team and opponent team colors.
     */
    private void setupAiIdentity(Game game, AIPlayer aiPlayer) {
        this.aiUsername = aiPlayer.getUsername().trim().toUpperCase();
        boolean p1IsAi = game.getParticipation1().getPlayer().getUsername().trim().equalsIgnoreCase(aiUsername);
        aiColor = p1IsAi ? game.getParticipation1().getChosenPawnColor() : game.getParticipation2().getChosenPawnColor();
        opponentColor = p1IsAi ? game.getParticipation2().getChosenPawnColor() : game.getParticipation1().getChosenPawnColor();
    }

    /**
     * Executes a full turn on a game instance.
     */
    private void applyTurn(Game game, Turn turn) {
        if (turn.getFirstMove() != null) executeSingleMoveOnBoard(game, turn.getFirstMove());
        if (turn.getSecondMove() != null) executeSingleMoveOnBoard(game, turn.getSecondMove());
    }

    /**
     * Maps and executes a specific move on a copied board.
     */
    private void executeSingleMoveOnBoard(Game game, Move m) {
        Peg start = game.getBoard().getPegPosition(m.getStartPeg().getYPosition(), m.getStartPeg().getXPosition());
        Peg dest = game.getBoard().getPegPosition(m.getDestinationPeg().getYPosition(), m.getDestinationPeg().getXPosition());
        if (start != null && dest != null && !start.getPawns().isEmpty()) {
            game.selectStartPeg(start);
            game.executeMove(dest);
        }
    }

    /**
     * Internal heuristic scoring for turn comparison.
     */
    private int getTurnScore(Turn t, boolean isAi, Game g) {
        if (t == null || t.getFirstMove() == null) return -99999;
        int s = evaluateSingleMove(t.getFirstMove(), isAi, g);
        if (t.getSecondMove() != null) s += evaluateSingleMove(t.getSecondMove(), isAi, g);
        return s;
    }

    /**
     * Counts finished pawns for win detection and bias calculation.
     */
    private int countFinishedPawns(Game game, PawnColor color) {
        int count = 0;
        for (Peg[] r : game.getBoard().getAllPegs()) {
            for (Peg f : r) {
                if (f != null && f.getYPosition() == 3 && !f.getPawns().isEmpty() &&
                        f.getUpperPawn().getPawnColor().equals(color)) count++;
            }
        }
        return count;
    }

    /**
     * Calculates fear bias based on opponent progress.
     */
    private double calculateThreatBias(Game game) {
        int oppFin = countFinishedPawns(game, opponentColor);
        int oppOnThreshold = 0;
        for (Peg[] row : game.getBoard().getAllPegs()) {
            for (Peg p : row) {
                if (p != null && p.getYPosition() == 2 && !p.getPawns().isEmpty() &&
                        p.getUpperPawn().getPawnColor().equals(opponentColor)) oppOnThreshold++;
            }
        }
        return Math.min(30.0, 1.0 + (oppFin * 6.0) + (oppOnThreshold * 4.0));
    }

    /**
     * MCTS Random rollout logic.
     */
    private double simulate(ZarocNode node, Random rnd) {
        Game simGame = node.getState().gameCopy();
        int turns = maxMoves;
        while (turns > 0) {
            InternalResult res = checkWinInternal(simGame);
            if (res == InternalResult.AI_WINS) return 1.0;
            if (res == InternalResult.OPP_WINS) return 0.0;
            List<Turn> opts = MoveGenerator.getAllLegalTurns(simGame);
            if (opts.isEmpty()) break;
            Turn t = (rnd.nextDouble() > 0.1) ? getHeuristicBestTurn(opts, simGame) : opts.get(rnd.nextInt(opts.size()));
            applyTurn(simGame, t);
            turns--;
        }
        return calculateProgressForAI(simGame);
    }

    /**
     * Checks if current state is a terminal victory.
     */
    private InternalResult checkWinInternal(Game game) {
        int ai = countFinishedPawns(game, aiColor);
        int opp = countFinishedPawns(game, opponentColor);
        return (opp >= 3) ? InternalResult.OPP_WINS : ((ai >= 3) ? InternalResult.AI_WINS : InternalResult.PLAYING);
    }

    /**
     * Direct win detection for defense.
     */
    private boolean canOpponentWinImmediately(Game game) {
        for (Turn t : MoveGenerator.getAllLegalTurns(game)) {
            Game test = game.gameCopy();
            applyTurn(test, t);
            if (checkWinInternal(test) == InternalResult.OPP_WINS) return true;
        }
        return false;
    }

    /**
     * Minimax depth-first search for forced losses.
     */
    private boolean isForcedLoss(Game state, int depth, boolean isAiTurn) {
        InternalResult res = checkWinInternal(state);
        if (res == InternalResult.OPP_WINS) return true;
        if (res == InternalResult.AI_WINS || depth == 0) return false;
        List<Turn> opts = MoveGenerator.getAllLegalTurns(state);
        if (opts.isEmpty()) return false;
        for (Turn t : opts) {
            Game n = state.gameCopy();
            applyTurn(n, t);
            if (isAiTurn) { if (!isForcedLoss(n, depth - 1, false)) return false; }
            else { if (isForcedLoss(n, depth - 1, true)) return true; }
        }
        return isAiTurn;
    }

    /**
     * Threaded execution of MCTS tasks.
     */
    private ZarocNode runMctsThread(Game baseGame, int iterations) {
        ZarocNode root = new ZarocNode(baseGame.gameCopy(), null, null);
        Random rnd = new Random();
        for (int i = 0; i < iterations; i++) {
            ZarocNode leaf = select(root);
            ZarocNode newNode = expand(leaf, rnd);
            backpropagate(newNode, simulate(newNode, rnd));
        }
        return root;
    }

    /**
     * Merges simulation results from multiple search roots.
     */
    private Turn getBestCombinedTurn(List<ZarocNode> roots, List<Turn> filteredOptions, Game actualGame) {
        Map<String, MoveStats> data = new HashMap<>();
        for (ZarocNode root : roots) {
            for (ZarocNode child : root.getChildren()) {
                String key = getTurnKey(child.getInboundTurn());
                if (filteredOptions.stream().noneMatch(o -> getTurnKey(o).equals(key))) continue;
                data.putIfAbsent(key, new MoveStats());
                MoveStats s = data.get(key);
                s.turn = child.getInboundTurn();
                s.visits += child.getVisits();
                s.totalScore += child.getScore();
            }
        }
        List<MoveStats> sorted = new ArrayList<>(data.values());
        sorted.sort((a, b) -> Double.compare(b.totalScore/b.visits, a.totalScore/a.visits));
        return sorted.isEmpty() ? filteredOptions.get(0) : sorted.get(0).turn;
    }

    /**
     * Greedy turn selection used for simulation rollouts.
     */
    private Turn getHeuristicBestTurn(List<Turn> opts, Game g) {
        boolean isAi = g.getCurrentTurn().getCurrentPlayer().getUsername().trim().equalsIgnoreCase(aiUsername);
        return opts.stream().max(Comparator.comparingInt(t -> getTurnScore(t, isAi, g))).orElse(opts.get(0));
    }

    /**
     * Updates path statistics for simulated outcomes.
     */
    private void backpropagate(ZarocNode node, double score) {
        ZarocNode curr = node;
        while (curr != null) {
            curr.addVisit();
            if (curr.getInboundTurn() != null) {
                boolean isAi = curr.getInboundTurn().getCurrentPlayer().getUsername().trim().equalsIgnoreCase(aiUsername);
                curr.addScore(isAi ? score : (1.0 - score));
            }
            curr = curr.getParent();
        }
    }

    /**
     * UCB selection for tree traversal.
     */
    private ZarocNode select(ZarocNode node) {
        while (!node.getChildren().isEmpty()) {
            node = node.getChildren().stream().max(Comparator.comparingDouble(c -> c.getUCBValue(ucbConstantValue))).get();
        }
        return node;
    }

    /**
     * Expands a node by exploring a new legal move.
     */
    private ZarocNode expand(ZarocNode node, Random rnd) {
        if (checkWinInternal(node.getState()) != InternalResult.PLAYING) return node;
        List<Turn> opts = MoveGenerator.getAllLegalTurns(node.getState());
        Collections.shuffle(opts, rnd);
        for (Turn t : opts) {
            if (node.getChildren().stream().noneMatch(c -> getTurnKey(t).equals(getTurnKey(c.getInboundTurn())))) {
                Game next = node.getState().gameCopy();
                applyTurn(next, t);
                ZarocNode child = new ZarocNode(next, node, t);
                node.addChild(child);
                return child;
            }
        }
        return node;
    }

    /**
     * Hashing function for turn identification in the simulation tree.
     */
    private String getTurnKey(Turn t) {
        if (t == null || t.getFirstMove() == null || t.getFirstMove().getStartPeg() == null) return "invalid_" + System.nanoTime();
        Move m1 = t.getFirstMove();
        String k = m1.getStartPeg().getXPosition() + "," + m1.getStartPeg().getYPosition() + ">" + m1.getDestinationPeg().getXPosition() + "," + m1.getDestinationPeg().getYPosition();
        if (t.getSecondMove() != null && t.getSecondMove().getStartPeg() != null) {
            Move m2 = t.getSecondMove();
            k += "|" + m2.getStartPeg().getXPosition() + "," + m2.getStartPeg().getYPosition() + ">" + m2.getDestinationPeg().getXPosition() + "," + m2.getDestinationPeg().getYPosition();
        }
        return k;
    }

    /**
     * Internal container for MCTS simulation data.
     */
    private static class MoveStats { Turn turn; int visits; double totalScore; }
}