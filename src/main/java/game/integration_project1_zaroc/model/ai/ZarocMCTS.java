package game.integration_project1_zaroc.model.ai;

import game.integration_project1_zaroc.model.boardinfo.Peg;
import game.integration_project1_zaroc.model.gameinfo.PawnColor;
import game.integration_project1_zaroc.model.gamelogic.Game;
import game.integration_project1_zaroc.model.gamelogic.Move;
import game.integration_project1_zaroc.model.gamelogic.Turn;
import game.integration_project1_zaroc.model.players.Difficulty;

import java.util.*;
import java.util.concurrent.*;

public class ZarocMCTS {

    private final ZarocNeuralNet neuralNet;
    private final int totalIterations;
    private final int numThreads = Runtime.getRuntime().availableProcessors() ;
    private double ucbConstant;
    private Random random;

    private String aiUsername;
    private PawnColor aiColor;
    private PawnColor opponentColor;

    private enum Result { PLAYING, AI_WINS, OPP_WINS }

    public ZarocMCTS(ZarocNeuralNet neuralNet, int totalIterations,double ucbConstant) {
        this.neuralNet = neuralNet;
        this.totalIterations = totalIterations;
        this.ucbConstant = ucbConstant;
        this.random = new Random();
    }

    /**
     * this method is basically the entire mcts cycle combined in a format that allows it to run on a thread in the threadpool
     * we go through all the phases of mcts , select -> expand -> rollout -> backpropogate
     * every thread will execute this method for the amount of itterations, this depends on the amount of threads and is calculated in
     * findBestTurn()
     * @param base -> this is the original game satate so basically the current situation
     * @param iterations -> how many times will the mcts cycle run
     * @param rootBeam -> list of legal turns
     * @return -> returns a new root node based on the current game state
     */
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

    /**
     * this method returns the most interesting node, so basically you calculate the ucb score (upper confidence bound) and compares it
     * to the other children, if there are no children it will just return the inout node
     * @param node -> start node
     * @return -> best node
     */
    private ZarocNode select(ZarocNode node) {
        while (!node.getChildren().isEmpty()) {

            ZarocNode bestChild = null;
            double bestUcb = -Double.MAX_VALUE;
            for (ZarocNode child : node.getChildren()) {
                double currentUcb = ucb(child);

                if (currentUcb > bestUcb) {
                    bestUcb = currentUcb;
                    bestChild = child;
                }
            }
            node = bestChild;
        }
        return node;
    }

    /**
     * this method calculates the ucb score for a node
     * the score depends on the ucbConstant the amount of visits in both parent and child nodes and the score from the child node
     * we optimised our ai player by playing with the ucb score to get a higher exploration instead of exploitation
     * @param child
     * @return
     */
    private double ucb(ZarocNode child) {
        if (child.getVisits() == 0) return Double.MAX_VALUE;
        ZarocNode parent = child.getParent();
        if (parent == null || parent.getVisits() == 0) return Double.MAX_VALUE;
        double exploitation = child.getScore() / child.getVisits();
        double exploration  = ucbConstant * Math.sqrt(Math.log(parent.getVisits()) / child.getVisits());
        return exploitation + exploration;
    }

    /**
     * the expand function is there to expand every leaf node, you get the rootbeam these are the turns we are going to play on the
     * current game state
     * @param leaf -> current game state
     * @param rootBeam -> turns we want to explore
     */
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

    /**
     * this method is used to explore the game (normally) we use a shortcut here because a rollout takes a lot
     * of time normally. normally you simulate untill a winning state but because it takes time we just use the neural
     * network to evaluate the non winning game state
     * @param node
     * @return score
     */
    private double rollout(ZarocNode node) {
        Result res = checkResult(node.getState());
        if (res == Result.AI_WINS)  return 1.0;
        if (res == Result.OPP_WINS) return 0.0;
        double v = neuralNet.evaluateBoard(node.getState(), aiUsername);
        return (v < 0) ? 0.5 : v;
    }

    /**
     * this makes sure every leaf node gets updated with their new values
     */
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
     * @param difficulty    chosen difficulty for the ai player
     * @return the best Turn found by MCTS
     */
    public Turn findBestTurn(Game rootState, List<Turn> beam,String aiUsername,Difficulty difficulty, PawnColor aiColor, PawnColor opponentColor) {
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
        /**
         *  this part collects the data from all the threads
         *  executo.invokeAll(tasks) -> combine all the tasks
         *  invokAll blocks all code from running until all threads are done
         *  "Future" is like a promise, it represents a result that will be calculated
         *  in the future, we use f.get() to wait for and retrieve the final result
         *  from each completed thread
         *  the future basically waits till there is a result
         */
        try {
            for (Future<ZarocNode> f : executor.invokeAll(tasks)) {
                roots.add(f.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown(); // we always need to make sure we END the threadpool
        }

        return pickBestTurn(roots, beam,difficulty);
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
    private Turn pickBestTurn(List<ZarocNode> roots, List<Turn> beam,Difficulty difficulty) {
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

        Turn best = null;
        List<MoveStats> dataList = new ArrayList<>();
        dataList.addAll(data.values());

        dataList.sort((s1, s2) -> {
            double score1 = (s1.visits == 0) ? 0.0 : ( s1.totalScore / s1.visits);
            double score2 = (s2.visits == 0) ? 0.0 : ( s2.totalScore / s2.visits);

            return Double.compare(score2, score1);
        });

        best = getTurnBasedOnDifficulty(dataList,difficulty);




        return best != null ? best : beam.get(0);
    }

    /**
     * this method is responsible for making the ai adjustable for each difficulty this prevents the ez ai
     * from playing like a grandmaster
     * @param dataList
     * @param difficulty
     * @return
     */
    private Turn getTurnBasedOnDifficulty(List<MoveStats> dataList, Difficulty difficulty){
        List<MoveStats> availableTurns =
                switch (difficulty){
                    case EASY -> dataList.subList(3, 11);
                    case MEDIUM -> dataList.subList(2,5);
                    case HARD -> dataList.subList(1,3);
                    case ELITE -> dataList.subList(0,0);
        };
        MoveStats chosenTurn = availableTurns.get(random.nextInt(0,availableTurns.size()));
        return chosenTurn.turn;
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