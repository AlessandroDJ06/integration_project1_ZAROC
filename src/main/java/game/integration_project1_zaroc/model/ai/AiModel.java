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

        int iterations = switch (difficulty) {
            case 0  -> 1000;
            case 1  -> 2000;
            case 2  -> 3000;
            case 3  -> 4000;
            default -> 500;
        };

        this.ucbConstant = switch (difficulty) {
            case 0  -> 4;
            case 1  -> 1.0;
            case 2  -> 1.2;
            case 3  -> Math.sqrt(2);
            default -> 1;
        };

        String modelPath = "/game/integration_project1_zaroc/neuralnetwork/zaroc_model_v6.onnx";

        System.out.println(modelPath);

        this.neuralNet = new ZarocNeuralNet(modelPath);
        this.mcts = new ZarocMCTS(neuralNet, iterations,ucbConstant);
    }

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

    private double scoreTurn(Turn t, Game game) {
        Game copy = game.gameCopy();
        applyTurn(copy, t);
        copy.switchCurrentPlayer();
        return neuralNet.evaluateBoard(copy, aiUsername);
    }

    private void setupIdentity(Game game, AIPlayer aiPlayer) {
        this.aiUsername = aiPlayer.getUsername().trim().toUpperCase();
        boolean p1IsAi = game.getParticipation1().getPlayer().getUsername().trim().equalsIgnoreCase(aiUsername);
        this.aiColor       = p1IsAi ? game.getParticipation1().getChosenPawnColor() : game.getParticipation2().getChosenPawnColor();
        this.opponentColor = p1IsAi ? game.getParticipation2().getChosenPawnColor() : game.getParticipation1().getChosenPawnColor();
    }

    private void applyTurn(Game game, Turn turn) {
        if (turn.getFirstMove()  != null) applyMove(game, turn.getFirstMove());
        if (turn.getSecondMove() != null) applyMove(game, turn.getSecondMove());
    }

    private void applyMove(Game game, Move m) {
        Peg start = game.getBoard().getPegPosition(m.getStartPeg().getYPosition(), m.getStartPeg().getXPosition());
        Peg dest  = game.getBoard().getPegPosition(m.getDestinationPeg().getYPosition(), m.getDestinationPeg().getXPosition());
        if (start != null && dest != null && !start.getPawns().isEmpty()) {
            game.selectStartPeg(start);
            game.executeMove(dest);
        }
    }
}