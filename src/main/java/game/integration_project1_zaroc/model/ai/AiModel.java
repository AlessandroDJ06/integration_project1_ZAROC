package game.integration_project1_zaroc.model.ai;

import game.integration_project1_zaroc.model.boardinfo.Peg;
import game.integration_project1_zaroc.model.gameinfo.PawnColor;
import game.integration_project1_zaroc.model.gamelogic.*;
import game.integration_project1_zaroc.model.players.AIPlayer;
import game.integration_project1_zaroc.model.players.Difficulty;

import java.util.*;

public class AiModel {

    private static final int BEAM_WIDTH = 30;

    private PawnColor aiColor;
    private PawnColor opponentColor;
    private String aiUsername;
    private Difficulty difficulty;

    private final ZarocNeuralNet neuralNet;
    private final ZarocMCTS mcts;
    private double ucbConstant;

    public AiModel(int difficulty, String name) {
        this.aiUsername = name.toUpperCase();
        this.difficulty = Difficulty.values()[difficulty];

        /**
         * this determines the amount of iterations mcts gets to evaluate the board based on difficulty
         */
        int iterations = switch (difficulty) {
            case 0  -> 500;
            case 1  -> 1000;
            case 2  -> 1500;
            case 3  -> 2000;
            default -> 750;
        };

        /**
         * this determines the ucb (upper confidence bound) based on difficulty, the higher the constant the more the model will explore
         * and te lower the constant the more it will exploit a good move. Normally you want it to be a bit 50/50 and sqrt of 2 is considerd
         * to be perfect balance between exploration and exploitation
         */
        this.ucbConstant = switch (difficulty) {
            case 0  -> 4.0;
            case 1  -> 3.0;
            case 2  -> 2.0;
            case 3  -> Math.sqrt(2);
            default -> 1;
        };

        String modelPath = "/game/integration_project1_zaroc/neuralnetwork/zaroc_model_v6.onnx";
        this.neuralNet = new ZarocNeuralNet(modelPath);
        this.mcts = new ZarocMCTS(neuralNet, iterations,ucbConstant);
    }

    /**
     * in this method we use mcts to find the most interesting moves, we use this to prevent the beam from getting to wide limiting
     * memmory usage and boosting performance
     * @param actualGame
     * @param aiPlayer
     * @return
     */
    public Turn getBestTurn(Game actualGame, AIPlayer aiPlayer) {
        setupIdentity(actualGame, aiPlayer);
        List<Turn> options = MoveGenerator.getAllLegalTurns(actualGame);
        if (options.isEmpty()) return null;

        Map<Turn, Double> scoreCache = new HashMap<>();
        for (Turn t : options) {
            scoreCache.put(t, scoreTurn(t, actualGame));
            System.out.println(scoreCache.get(t));
        }

        options.sort((a, b) -> Double.compare(scoreCache.get(b), scoreCache.get(a)));
        List<Turn> beam = options.subList(0, Math.min(options.size(), BEAM_WIDTH));

        return mcts.findBestTurn(actualGame, beam, aiUsername,difficulty,aiColor, opponentColor);
    }

    /**
     * this method is used to score a turn using the neural network, first of all we create the game copy and pass it to evaluate board where
     * the bord will be encoded and than evaluated by the model
     * @param turn -> turn that will be played on the game copy (this will be a turn from ALL legal moves)
     * @param game -> a copy from the original game
     * @return
     */
    private double scoreTurn(Turn turn, Game game) {
        Game copy = game.gameCopy();
        applyTurn(copy, turn);
        copy.switchCurrentPlayer();
        return neuralNet.evaluateBoard(copy, aiUsername);
    }

    /**
     * To be able to calculate the right moves for the ai player we need to tell the model who the ai player is
     * this allows the model te know what color the ai player is and what color the player is.
     * @param game -> copy from the original game
     * @param aiPlayer -> ai player object containing all info needed
     */
    private void setupIdentity(Game game, AIPlayer aiPlayer) {
        this.aiUsername = aiPlayer.getUsername().trim().toUpperCase();
        boolean p1IsAi = game.getParticipation1().getPlayer().getUsername().trim().equalsIgnoreCase(aiUsername);
        this.aiColor       = p1IsAi ? game.getParticipation1().getChosenPawnColor() : game.getParticipation2().getChosenPawnColor();
        this.opponentColor = p1IsAi ? game.getParticipation2().getChosenPawnColor() : game.getParticipation1().getChosenPawnColor();
    }

    /**
     * because zaroc had 2 moves each turn we need a helper method that allows 2 moves to be played
     * @param game -> game copy that will be passed to applymove
     * @param turn -> the turn that will be played
     */
    private void applyTurn(Game game, Turn turn) {
        if (turn.getFirstMove()  != null) applyMove(game, turn.getFirstMove());
        if (turn.getSecondMove() != null) applyMove(game, turn.getSecondMove());
    }

    /**
     * this method is used to apply a move to the game copy this is purely ti evaluate the game state eventually
     * @param game -> game copy
     * @param move -> move that will be executed
     */
    private void applyMove(Game game, Move move) {
        Peg start = game.getBoard().getPegPosition(move.getStartPeg().getYPosition(), move.getStartPeg().getXPosition());
        Peg dest  = game.getBoard().getPegPosition(move.getDestinationPeg().getYPosition(), move.getDestinationPeg().getXPosition());
        if (start != null && dest != null && !start.getPawns().isEmpty()) {
            game.selectStartPeg(start);
            game.executeMove(dest);
        }
    }
}