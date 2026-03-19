package game.integration_project1_zaroc.model.ai;

import game.integration_project1_zaroc.model.gameinfo.GameStatus;
import game.integration_project1_zaroc.model.gamelogic.*;
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
        // We starten de boom vanaf de huidige stand
        ZarocNode root = new ZarocNode(actualGame.gameCopy(), null, null);

        for (int i = 0; i < iterations; i++) {
            ZarocNode leaf = select(root);
            Player winner = simulate(leaf);
            backpropagate(leaf, winner);
        }

        // De beste zet is de eerste move van het meest bezochte kind
        ZarocNode bestChild = null;
        int maxVisits = -1;

        for (ZarocNode child : root.getChildren()) {
            if (child.getVisits() > maxVisits) {
                maxVisits = child.getVisits();
                bestChild = child;
            }
        }

        // Veiligheidscheck: als er geen zetten zijn, return null
        if (bestChild == null) return null;

        // We geven de eerste move terug (zodat de AI per move kan herberekenen)
        return bestChild.getInboundTurn().getFirstMove();
    }

    private ZarocNode select(ZarocNode node) {
        // 1. Selection: Volg de boom via UCB1 scores
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

        // 2. Expansion: Als we een blad bereiken, voeg een nieuwe node toe
        if (node.getState().getStatus() == GameStatus.PLAYING) {
            List<Turn> possibleTurns = MoveGenerator.getAllLegalTurns(node.getState());
            if (!possibleTurns.isEmpty()) {
                // Pak een willekeurige nog niet geprobeerde turn (simpele expansie)
                Turn turnToTry = possibleTurns.get(random.nextInt(possibleTurns.size()));
                Game nextState = node.getState().gameCopy();

                // Voer de beurt uit op de kopie
                nextState.executeMove(
                        nextState.getBoard().getPegPosition(turnToTry.getFirstMove().getStartPeg().getXPosition(), turnToTry.getFirstMove().getStartPeg().getYPosition()),
                        nextState.getBoard().getPegPosition(turnToTry.getFirstMove().getDestinationPeg().getXPosition(), turnToTry.getFirstMove().getDestinationPeg().getYPosition())
                );
                // Tweede move van de beurt
                nextState.executeMove(
                        nextState.getBoard().getPegPosition(turnToTry.getSecondMove().getStartPeg().getXPosition(), turnToTry.getSecondMove().getStartPeg().getYPosition()),
                        nextState.getBoard().getPegPosition(turnToTry.getSecondMove().getDestinationPeg().getXPosition(), turnToTry.getSecondMove().getDestinationPeg().getYPosition())
                );

                ZarocNode newNode = new ZarocNode(nextState, node, turnToTry);
                node.addChild(newNode);
                return newNode;
            }
        }
        return node;
    }

    private Player simulate(ZarocNode node) {
        // 3. Simulation (Rollout): Speel willekeurig uit
        Game simGame = node.getState().gameCopy();
        int maxMoves = 50; // Beveiliging tegen oneindige loops

        while (simGame.getStatus() == GameStatus.PLAYING && maxMoves > 0) {
            List<Turn> options = MoveGenerator.getAllLegalTurns(simGame);
            if (options.isEmpty()) break;

            Turn randomTurn = options.get(random.nextInt(options.size()));
            simGame.executeMove(
                    simGame.getBoard().getPegPosition(randomTurn.getFirstMove().getStartPeg().getXPosition(), randomTurn.getFirstMove().getStartPeg().getYPosition()),
                    simGame.getBoard().getPegPosition(randomTurn.getFirstMove().getDestinationPeg().getXPosition(), randomTurn.getFirstMove().getDestinationPeg().getYPosition())
            );
            simGame.executeMove(
                    simGame.getBoard().getPegPosition(randomTurn.getSecondMove().getStartPeg().getXPosition(), randomTurn.getSecondMove().getStartPeg().getYPosition()),
                    simGame.getBoard().getPegPosition(randomTurn.getSecondMove().getDestinationPeg().getXPosition(), randomTurn.getSecondMove().getDestinationPeg().getYPosition())
            );
            maxMoves--;
        }
        return simGame.getWinner();
    }

    private void backpropagate(ZarocNode node, Player winner) {
        // 4. Backpropagation: Werk scores bij naar boven toe
        ZarocNode temp = node;
        while (temp != null) {
            temp.addVisit();
            // Als de winnaar de speler is van de vorige beurt, krijgt deze node punten
            if (winner != null && temp.getParent() != null &&
                    winner.equals(temp.getParent().getState().getCurrentTurn().getCurrentPlayer())) {
                temp.addScore(1.0);
            } else if (winner == null) {
                temp.addScore(0.5); // Gelijkspel/onbeslist
            }
            temp = temp.getParent();
        }
    }
}