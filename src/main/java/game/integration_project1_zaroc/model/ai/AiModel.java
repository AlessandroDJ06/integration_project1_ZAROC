package game.integration_project1_zaroc.model.ai;

import game.integration_project1_zaroc.model.gameinfo.GameParticipation;
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
 * The core AI logic engine for the Zaroc game.
 * <p>
 * This model utilizes <b>"The Breaker"</b> logic, a multi-layered decision-making system
 * designed to prevent defensive stalemates. It combines:
 * <ul>
 * <li><b>Minimax:</b> For short-term tactical safety and blunder prevention.</li>
 * <li><b>Monte Carlo Tree Search (MCTS):</b> For long-term statistical strategy.</li>
 * <li><b>Heuristic Weighting:</b> To simulate aggression, sabotage, and progression.</li>
 * </ul>
 *
 * @author Alessandro De Jongh
 * @version 3.0
 * @see MoveGenerator
 * @see ZarocNode
 */
public class AiModel {
    private final int totalIterations;
    private final int maxMoves = 250;
    private final double ucbConstantValue = Math.sqrt(2);
    private final int numThreads = Runtime.getRuntime().availableProcessors();
    private final int minimaxDepth;

    // Fixed Heuristic Weightings
    private static final int SCORE_WIN = 100000;
    private static final int BASE_WEIGHT_ADVANCE_ROW_2 = 500;
    private static final int BASE_WEIGHT_CONTROL_OPPONENT = 6000;
    private static final int BASE_WEIGHT_BLOCK_THRESHOLD = 90000;
    private static final int BASE_PENALTY_OPPONENT_NEAR_FINISH = -20000;
    private static final int BASE_WEIGHT_SABOTAGE_DRAG = 4000;

    /** PROGRESS BONUS: A reward weight given for forward movement to encourage active gameplay. */
    private static final int WEIGHT_PROGRESS_STEP = 1500;

    private PawnColor aiColor;
    private PawnColor opponentColor;
    private String aiUsername;

    /**
     * Enumeration used to categorize the outcome of game state simulations.
     */
    private enum InternalResult { PLAYING, AI_WINS, OPP_WINS }

    /**
     * Constructs the AI model with settings derived from the chosen difficulty.
     *
     * @param difficulty The difficulty level (0: Easy, 1: Medium, 2: Hard).
     */
    public AiModel(int difficulty) {
        this.totalIterations = switch (difficulty) {
            case 0 -> 25000;
            case 1 -> 100000;
            case 2 -> 400000;
            default -> 10000;
        };

        this.minimaxDepth = switch (difficulty) {
            case 0 -> 2;
            case 1 -> 3;
            case 2 -> 4;
            default -> 3;
        };
    }

    /**
     * Entry point for the AI to calculate the best possible turn.
     * Processes moves through four logical filters: Win detection, Emergency blocking,
     * Tactical safety, and Statistical simulation.
     *
     * @param actualGame The current live {@link Game} instance.
     * @param aiPlayer   The {@link AIPlayer} requesting the turn.
     * @return The most advantageous legal {@link Turn} identified.
     */
    public Turn getBestTurn(Game actualGame, AIPlayer aiPlayer) {
        setupAiIdentity(actualGame, aiPlayer);
        List<Turn> options = MoveGenerator.getAllLegalTurns(actualGame);
        if (options.isEmpty()) return null;

        /** [1] OLYMPIC GOLD: Search for an immediate winning move. */
        for (Turn t : options) {
            Game testGame = actualGame.gameCopy();
            applyTurn(testGame, t);
            if (checkWinInternal(testGame) == InternalResult.AI_WINS) {
                System.out.println("AI LOG: [STRATEGY: OLYMPIC GOLD] - Immediate victory path found.");
                return t;
            }
        }

        /** [2] SMART EMERGENCY OVERRIDE: Intervene if the opponent is one turn from winning. */
        if (countFinishedPawns(actualGame, opponentColor) >= 2) {
            List<Turn> survivalOptions = new ArrayList<>();
            for (Turn t : options) {
                Game testGame = actualGame.gameCopy();
                applyTurn(testGame, t);
                if (!canOpponentWinImmediately(testGame)) survivalOptions.add(t);
            }
            if (!survivalOptions.isEmpty() && survivalOptions.size() < options.size()) {
                System.out.println("AI LOG: [TACTIC: EMERGENCY OVERRIDE] - Opponent win threat detected; forcing defensive response.");
                options = survivalOptions;
            }
        }

        /** [3] TACTICAL FILTER: Use Minimax look-ahead to prune moves leading to forced losses. */
        List<Turn> safeTurns = new ArrayList<>();
        for (Turn t : options) {
            Game testGame = actualGame.gameCopy();
            applyTurn(testGame, t);
            if (!isForcedLoss(testGame, minimaxDepth, false)) safeTurns.add(t);
        }

        if (!safeTurns.isEmpty() && safeTurns.size() < options.size()) {
            System.out.println("AI LOG: [TACTIC: TACTICAL FILTER] - Filtered " + (options.size() - safeTurns.size()) + " moves identified as future blunders.");
            options = safeTurns;
        } else if (safeTurns.isEmpty()) {
            System.out.println("AI LOG: [STRATEGY: SURVIVAL INSTINCT] - No tactically safe moves found; falling back to best heuristic sabotage.");
            options.sort((a, b) -> Integer.compare(getTurnScore(b, true, actualGame), getTurnScore(a, true, actualGame)));
            return options.get(0);
        }

        /** [4] SCORING DRIVE: Prioritize moves that result in a pawn finishing the game. */
        int currentScore = countFinishedPawns(actualGame, aiColor);
        List<Turn> scoringTurns = new ArrayList<>();
        for (Turn t : options) {
            Game testGame = actualGame.gameCopy();
            applyTurn(testGame, t);
            if (countFinishedPawns(testGame, aiColor) > currentScore) scoringTurns.add(t);
        }
        if (!scoringTurns.isEmpty()) {
            System.out.println("AI LOG: [STRATEGY: SCORING DRIVE] - Prioritizing pawn advancement into the finish line.");
            options = scoringTurns;
            if (options.size() == 1) return options.get(0);
        }

        /** [5] HYBRID MCTS: Execute statistical simulations for long-term strategic depth. */
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
        System.out.println("AI LOG: [STRATEGY: HYBRID MCTS] - Final decision based on win probability across " + totalIterations + " simulations.");
        return finalTurn;
    }

    /**
     * Calculates a heuristic score for a single move based on board position and threats.
     *
     * @param m    The {@link Move} to evaluate.
     * @param isAi True if scoring for the AI, false for the opponent.
     * @param g    The current {@link Game} state.
     * @return An integer representing the move's value.
     */
    private int evaluateSingleMove(Move m, boolean isAi, Game g) {
        if (m == null || m.getStartPeg() == null) return 0;
        Peg dest = m.getDestinationPeg();
        Peg start = m.getStartPeg();
        int row = dest.getYPosition();
        double tb = calculateThreatBias(g);

        if (isAi) {
            int s = (row == 3) ? SCORE_WIN * 10 : ((row == 2) ? BASE_WEIGHT_ADVANCE_ROW_2 : 0);

            // Reward progression and punish regression
            if (row > start.getYPosition()) s += WEIGHT_PROGRESS_STEP;
            else if (row < start.getYPosition()) s -= WEIGHT_PROGRESS_STEP;

            // Reward "Sabotage Drag" (carrying opponent pawns away from their goal)
            int hostages = 0;
            for (Pawn p : start.getPawns()) {
                if (p.getPawnColor().equals(opponentColor)) hostages++;
            }
            if (hostages > 0) s += (int) (BASE_WEIGHT_SABOTAGE_DRAG * hostages * (row + 1));

            // Reward landing on an opponent to gain control
            if (!dest.getPawns().isEmpty() && dest.getUpperPawn().getPawnColor().equals(opponentColor)) {
                double mFactor = (row == 2) ? (tb * 4) : tb;
                s += (int) (BASE_WEIGHT_CONTROL_OPPONENT * mFactor);
                if (row == 2) s += (int) (BASE_WEIGHT_BLOCK_THRESHOLD * tb);
            }
            return s;
        }
        return (row == 3) ? (int) (-SCORE_WIN * 15 * tb) : ((row == 2) ? (int) (BASE_PENALTY_OPPONENT_NEAR_FINISH * tb) : 0);
    }

    /**
     * Evaluates a board state at the end of a simulation rollout.
     *
     * @param game The {@link Game} state to evaluate.
     * @return A win probability percentage (0.0 to 1.0).
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
                    // Reward "Tower Hostages" held in own stacks
                    for (Pawn hostage : p.getPawns()) {
                        if (hostage.getPawnColor().equals(opponentColor)) aiScore += (val * 0.2);
                    }
                } else {
                    double m = (p.getYPosition() == 2 && oppFin >= 2) ? (threatBias * 4) : 1.0;
                    oppScore += (val * m);
                }
            }
        }
        return (oppFin >= 3) ? 0.0 : Math.max(0.01, Math.min(0.99, 0.5 + (aiScore - oppScore) / 15000000.0));
    }

    /**
     * Generates a unique string key for a turn, used for MCTS move mapping.
     *
     * @param t The {@link Turn} to identify.
     * @return A unique identification string.
     */
    private String getTurnKey(Turn t) {
        if (t == null || t.getFirstMove() == null || t.getFirstMove().getStartPeg() == null) return "invalid_" + System.nanoTime();
        Move m1 = t.getFirstMove();
        String k = m1.getStartPeg().getXPosition() + "," + m1.getStartPeg().getYPosition() + ">" +
                m1.getDestinationPeg().getXPosition() + "," + m1.getDestinationPeg().getYPosition();
        if (t.getSecondMove() != null && t.getSecondMove().getStartPeg() != null) {
            Move m2 = t.getSecondMove();
            k += "|" + m2.getStartPeg().getXPosition() + "," + m2.getStartPeg().getYPosition() + ">" +
                    m2.getDestinationPeg().getXPosition() + "," + m2.getDestinationPeg().getYPosition();
        }
        return k;
    }

    /**
     * Counts how many pawns of a given color are in the finish zone.
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
     * Calculates the "Fear Factor" of an opponent reaching the finish zone.
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
     * Scores a turn immediately using heuristics.
     */
    private int getTurnScore(Turn t, boolean isAi, Game g) {
        if (t == null || t.getFirstMove() == null) return -99999;
        int s = evaluateSingleMove(t.getFirstMove(), isAi, g);
        if (t.getSecondMove() != null) s += evaluateSingleMove(t.getSecondMove(), isAi, g);
        return s;
    }

    /**
     * Executes an MCTS simulation (Rollout) on a game state.
     *
     * @return Win result for the AI (0.0 to 1.0).
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
     * Determines current victory status.
     */
    private InternalResult checkWinInternal(Game game) {
        int ai = countFinishedPawns(game, aiColor);
        int opp = countFinishedPawns(game, opponentColor);
        return (opp >= 3) ? InternalResult.OPP_WINS : ((ai >= 3) ? InternalResult.AI_WINS : InternalResult.PLAYING);
    }

    /**
     * Checks if the active player can finish on their next turn.
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
     * Recursive Minimax search depth-first.
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
     * Orchestrates a single thread of MCTS simulations.
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
     * Aggregates simulation data to pick the turn with the highest statistical win rate.
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
     * Identifies colors and usernames for AI decision context.
     */
    private void setupAiIdentity(Game game, AIPlayer aiPlayer) {
        this.aiUsername = aiPlayer.getUsername().trim();
        boolean p1IsAi = game.getParticipation1().getPlayer().getUsername().trim().equalsIgnoreCase(aiUsername);
        aiColor = p1IsAi ? game.getParticipation1().getChosenPawnColor() : game.getParticipation2().getChosenPawnColor();
        opponentColor = p1IsAi ? game.getParticipation2().getChosenPawnColor() : game.getParticipation1().getChosenPawnColor();
    }

    /**
     * Applies a turn's moves to a game state.
     */
    private void applyTurn(Game game, Turn turn) {
        if (turn.getFirstMove() != null) executeSingleMoveOnBoard(game, turn.getFirstMove());
        if (turn.getSecondMove() != null) executeSingleMoveOnBoard(game, turn.getSecondMove());
    }

    /**
     * Physically maps move pegs to a copied board state and executes.
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
     * Simulation move selection (greedy heuristic with small random chance).
     */
    private Turn getHeuristicBestTurn(List<Turn> opts, Game g) {
        boolean isAi = g.getCurrentTurn().getCurrentPlayer().getUsername().trim().equalsIgnoreCase(aiUsername);
        return opts.stream().max(Comparator.comparingInt(t -> getTurnScore(t, isAi, g))).orElse(opts.get(0));
    }

    /**
     * Updates path statistics for MCTS.
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
     * Traverses tree using UCB1 selection.
     */
    private ZarocNode select(ZarocNode node) {
        while (!node.getChildren().isEmpty()) {
            node = node.getChildren().stream().max(Comparator.comparingDouble(c -> c.getUCBValue(ucbConstantValue))).get();
        }
        return node;
    }

    /**
     * Expands the tree by picking an unexplored legal move.
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
     * Internal container for MCTS move data.
     */
    private static class MoveStats { Turn turn; int visits; double totalScore; }
}