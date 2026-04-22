package game.integration_project1_zaroc.model.ai;

import game.integration_project1_zaroc.model.boardinfo.Peg;
import game.integration_project1_zaroc.model.gameinfo.PawnColor;
import game.integration_project1_zaroc.model.gamelogic.Game;
import game.integration_project1_zaroc.model.gamelogic.Move;
import game.integration_project1_zaroc.model.gamelogic.Turn;

import java.util.*;
import java.util.concurrent.*;

public class ZarocMCTS {

    private final ZarocNeuralNet neuralNet;
    private final int totalIterations;
    private final int numThreads = Runtime.getRuntime().availableProcessors() / 2 ;
    private final double ucbConstant = Math.sqrt(2);

    private String aiUsername;
    private PawnColor aiColor;
    private PawnColor opponentColor;

    private enum Result { PLAYING, AI_WINS, OPP_WINS }

    public ZarocMCTS(ZarocNeuralNet neuralNet, int totalIterations) {
        this.neuralNet = neuralNet;
        this.totalIterations = totalIterations;
    }

    private ZarocNode runMctsThread(Game base, int iterations, List<Turn> rootBeam) {
        ZarocNode root = new ZarocNode(base.gameCopy(), null, null);
        expand(root, rootBeam);
        for (int i = 0; i < iterations; i++) {
            ZarocNode leaf = select(root);
            expand(leaf, null);
            double score = rollout(leaf);
            backpropagate(leaf, score);
        }
        return root;
    }

    private ZarocNode select(ZarocNode node) {
        while (!node.getChildren().isEmpty()) {
            node = node.getChildren().stream()
                    .max(Comparator.comparingDouble(child -> ucb(child)))
                    .get();
        }
        return node;
    }

    private double ucb(ZarocNode child) {
        if (child.getVisits() == 0) return Double.MAX_VALUE;
        ZarocNode parent = child.getParent();
        if (parent == null || parent.getVisits() == 0) return Double.MAX_VALUE;
        double exploitation = child.getScore() / child.getVisits();
        double exploration  = ucbConstant * Math.sqrt(Math.log(parent.getVisits()) / child.getVisits());
        return exploitation + exploration;
    }

    private void expand(ZarocNode leaf, List<Turn> rootBeam) {
        if (checkResult(leaf.getState()) != Result.PLAYING) return;
        if (!leaf.getChildren().isEmpty()) return;

        List<Turn> opts = (leaf.getParent() == null && rootBeam != null)
                ? new ArrayList<>(rootBeam)
                : MoveGenerator.getAllLegalTurns(leaf.getState());

        if (opts.isEmpty()) return;

        for (Turn t : opts) {
            Game next = leaf.getState().gameCopy();
            applyTurn(next, t);
            next.switchCurrentPlayer();
            leaf.addChild(new ZarocNode(next, leaf, t));
        }
    }

    private double rollout(ZarocNode node) {
        Result res = checkResult(node.getState());
        if (res == Result.AI_WINS)  return 1.0;
        if (res == Result.OPP_WINS) return 0.0;
        double v = neuralNet.evaluateBoard(node.getState(), aiUsername);
        return (v < 0) ? 0.5 : v;
    }

    private void backpropagate(ZarocNode node, double score) {
        ZarocNode curr = node;
        while (curr != null) {
            curr.addVisit();
            curr.addScore(score);
            curr = curr.getParent();
        }
    }
    /**
     * Starts the MCTS search for the best turn from the current game state.
     * Runs multiple MCTS trees in parallel using a thread pool.
     * Each thread builds its own independent tree and the results are combined afterwards.
     * this makes sure the model creates a good statistical indication if a move is good or not , while
     * making sure the ui isnt laggy or crashes
     *
     * @param rootState    the current game state to search from
     * @param beam         pre-filtered list of candidate turns (top N by value network)
     * @param aiUsername   username of the AI player
     * @param aiColor      pawn color of the AI player
     * @param opponentColor pawn color of the opponent
     * @return the best Turn found by MCTS
     */
    public Turn findBestTurn(Game rootState, List<Turn> beam,
                             String aiUsername, PawnColor aiColor, PawnColor opponentColor) {
        this.aiUsername = aiUsername;
        this.aiColor = aiColor;
        this.opponentColor = opponentColor;

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Callable<ZarocNode>> tasks = new ArrayList<>();
        int itersPerThread = Math.max(1, totalIterations / numThreads);

        for (int i = 0; i < numThreads; i++) {
            tasks.add(() -> runMctsThread(rootState, itersPerThread, beam));
        }

        List<ZarocNode> roots = new ArrayList<>();
        try {
            for (Future<ZarocNode> f : executor.invokeAll(tasks)) {
                roots.add(f.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        return pickBestTurn(roots, beam);
    }

    /**
     * Combines the results of all parallel MCTS trees and picks the best turn.
     *
     * For each candidate turn in the beam, the visits and scores from all trees
     * are aggregated using a string key (getTurnKey) since Turn objects from
     * different trees are different objects in memory but may represent the same move.
     *
     * The best turn is selected by highest average score (totalScore / visits).
     *
     * We evaluate with these criteria
     *   1. visits = 0                             — skip turns that were never visited
     *   2. (stats.totalScore / stats.visits)      — find the turn with highest average score
     *   3. (best.totalScore / best.visits)        — this is the highest average of best turn , if a new turn is higher its set as best turn
     *   4. best != null ? best.turn : beam.get(0) — fallback to first beam turn if no best moves are found
     *
     * @param roots list of root nodes from each parallel MCTS thread
     * @param beam  the original candidate turns used as filter
     * @return the Turn with the highest average MCTS score
     */
    private Turn pickBestTurn(List<ZarocNode> roots, List<Turn> beam) {
        Set<String> beamKeys = new HashSet<>();
        for (Turn t : beam) {
            String k = getTurnKey(t);
            if (k != null) beamKeys.add(k);
        }

        Map<String, MoveStats> data = new HashMap<>();

        for (ZarocNode root : roots) {
            for (ZarocNode child : root.getChildren()) {
                String key = getTurnKey(child.getInboundTurn());
                if (key == null || !beamKeys.contains(key)) continue;

                data.putIfAbsent(key, new MoveStats());
                MoveStats s = data.get(key);
                s.turn = child.getInboundTurn();
                s.visits += child.getVisits();
                s.totalScore += child.getScore();
            }
        }

        if (data.isEmpty()) return beam.get(0);

        MoveStats best = null;
        for (MoveStats stats : data.values()) {
            if (stats.visits == 0) continue;
            if (best == null || (stats.totalScore / stats.visits) > (best.totalScore / best.visits)) {
                best = stats;
            }
        }
        return best != null ? best.turn : beam.get(0);
    }

    /**
     * this funtion utilises the countFinished to check who won the game state, we use a different function than in
     * the main game as you can see , this is for simplicity and to make sure the game state isnt messed with
     * @param game
     * @return
     */
    private Result checkResult(Game game) {
        int ai  = countFinished(game, aiColor);
        int opp = countFinished(game, opponentColor);
        if (opp >= 3) return Result.OPP_WINS;
        if (ai  >= 3) return Result.AI_WINS;
        return Result.PLAYING;
    }

    /**
     * this function checks the win condition for the ai , the reason we use this instead of the function in game is that
     * this doesnt mess with the game state , it just counts the pawns in the finish and checks for a win
     * @param game game state (copy of the original game)
     * @param color tells wich color it needs to count
     * @return
     */
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

    /**
     * because zaroc allows players to do 2 moves each turn we need to apply 2 moves to each turn , the second
     * move is optional in some use cases , for example the last turn of the game
     * @param game game state (copy of game)
     * @param turn
     */
    private void applyTurn(Game game, Turn turn) {
        if (turn.getFirstMove()  != null) applyMove(game, turn.getFirstMove());
        if (turn.getSecondMove() != null) applyMove(game, turn.getSecondMove());
    }

    /**
     * this function applies a move to the copied game state , this will be used to expand the tree
     * @param game game state (copy of game)
     * @param move
     */
    private void applyMove(Game game, Move move) {
        Peg start = game.getBoard().getPegPosition(
                move.getStartPeg().getYPosition(), move.getStartPeg().getXPosition());
        Peg dest  = game.getBoard().getPegPosition(
                move.getDestinationPeg().getYPosition(), move.getDestinationPeg().getXPosition());
        if (start != null && dest != null && !start.getPawns().isEmpty()) {
            game.selectStartPeg(start);
            game.executeMove(dest);
        }
    }


    /**
     * This method is used by MCTS to compare the turns with each other, string are compareable with equals
     * turns not ( in theory we could also use a comperator but i think this is easier to split the logic)
     * @param turn
     * @return
     */
    private static String getTurnKey(Turn turn) {
        if (turn == null || turn.getFirstMove() == null
                || turn.getFirstMove().getStartPeg() == null
                || turn.getFirstMove().getDestinationPeg() == null) return null;

        Move m1 = turn.getFirstMove();
        String key = m1.getStartPeg().getXPosition() + "," + m1.getStartPeg().getYPosition()
                + ">" + m1.getDestinationPeg().getXPosition() + "," + m1.getDestinationPeg().getYPosition();

        if (turn.getSecondMove() != null && turn.getSecondMove().getStartPeg() != null
                && turn.getSecondMove().getDestinationPeg() != null) {
            Move m2 = turn.getSecondMove();
            key += "|" + m2.getStartPeg().getXPosition() + "," + m2.getStartPeg().getYPosition()
                    + ">" + m2.getDestinationPeg().getXPosition() + "," + m2.getDestinationPeg().getYPosition();
        }
        return key;
    }

    private static class MoveStats {
        Turn turn;
        int visits;
        double totalScore;
    }
}