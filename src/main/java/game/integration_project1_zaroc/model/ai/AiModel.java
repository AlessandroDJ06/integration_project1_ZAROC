package game.integration_project1_zaroc.model.ai;

import game.integration_project1_zaroc.model.gameinfo.GameStatus;
import game.integration_project1_zaroc.model.gamelogic.*;
import game.integration_project1_zaroc.model.boardinfo.Peg;
import game.integration_project1_zaroc.model.players.Player;
import java.util.List;
import java.util.Random;

public class AiModel {
    private final int iterations;
    private final Random random = new Random();

    public AiModel(int difficulty) {
        this.iterations = switch (difficulty) {
            case 1 -> 200;
            case 2 -> 1500;
            case 3 -> 6000;
            default -> 1000;
        };
    }

    public Move getBestMove(Game actualGame) {
        ZarocNode root = new ZarocNode(actualGame.gameCopy(), null, null);

        for (int i = 0; i < iterations; i++) {
            ZarocNode leaf = select(root);
            Player winner = simulate(leaf);
            backpropagate(leaf, winner);
        }

        ZarocNode bestChild = null;
        int maxVisits = -1;

        for (ZarocNode child : root.getChildren()) {
            if (child.getVisits() > maxVisits) {
                maxVisits = child.getVisits();
                bestChild = child;
            }
        }

        if (bestChild == null) return null;

        return bestChild.getInboundTurn().getFirstMove();
    }

    private ZarocNode select(ZarocNode node) {
        while (!node.getChildren().isEmpty()) {
            ZarocNode bestUCB = null;
            double bestValue = Double.NEGATIVE_INFINITY;
            for (ZarocNode child : node.getChildren()) {
                if (child.getUCBValue() > bestValue) {
                    bestValue = child.getUCBValue();
                    bestUCB = child;
                }
            }
            node = bestUCB;
        }

        if (node.getState().getStatus() == GameStatus.PLAYING) {
            List<Turn> possibleTurns = MoveGenerator.getAllLegalTurns(node.getState());
            if (!possibleTurns.isEmpty()) {
                Turn turnToTry = possibleTurns.get(random.nextInt(possibleTurns.size()));
                Game nextState = node.getState().gameCopy();
                executeFullTurnOnSim(nextState, turnToTry);

                ZarocNode newNode = new ZarocNode(nextState, node, turnToTry);
                node.addChild(newNode);
                return newNode;
            }
        }
        return node;
    }

    private Player simulate(ZarocNode node) {
        Game simGame = node.getState().gameCopy();
        int maxMoves = 50;

        while (simGame.getStatus() == GameStatus.PLAYING && maxMoves > 0) {
            List<Turn> options = MoveGenerator.getAllLegalTurns(simGame);
            if (options.isEmpty()) break;

            Turn randomTurn = options.get(random.nextInt(options.size()));
            executeFullTurnOnSim(simGame, randomTurn);

            maxMoves--;
        }
        return simGame.getWinner();
    }

    private void executeFullTurnOnSim(Game game, Turn turn) {
        if (turn == null || turn.getFirstMove() == null) return;

        Peg s1 = game.getBoard().getPegPosition(
                turn.getFirstMove().getStartPeg().getYPosition(),
                turn.getFirstMove().getStartPeg().getXPosition()
        );
        Peg d1 = game.getBoard().getPegPosition(
                turn.getFirstMove().getDestinationPeg().getYPosition(),
                turn.getFirstMove().getDestinationPeg().getXPosition()
        );

        if (s1 != null && d1 != null && !s1.getPawns().isEmpty()) {
            game.executeMove(s1, d1);
        }

        if (turn.getSecondMove() != null && game.getStatus() == GameStatus.PLAYING) {
            Peg s2 = game.getBoard().getPegPosition(turn.getSecondMove().getStartPeg().getYPosition(), turn.getSecondMove().getStartPeg().getXPosition());
            Peg d2 = game.getBoard().getPegPosition(turn.getSecondMove().getDestinationPeg().getYPosition(), turn.getSecondMove().getDestinationPeg().getXPosition());

            if (s2 != null && d2 != null && !s2.getPawns().isEmpty()) {
                game.executeMove(s2, d2);
            }
        }
    }

    private void backpropagate(ZarocNode node, Player winner) {
        ZarocNode temp = node;
        while (temp != null) {
            temp.addVisit();
            if (winner != null && temp.getParent() != null) {
                Player playerWhoMoved = temp.getParent().getState().getCurrentTurn().getCurrentPlayer();
                if (winner.equals(playerWhoMoved)) {
                    temp.addScore(1.0);
                }
            } else if (winner == null) {
                temp.addScore(0.5);
            }
            temp = temp.getParent();
        }
    }
}