package game.integration_project1_zaroc.model.ai;

import game.integration_project1_zaroc.model.boardinfo.Pawn;
import game.integration_project1_zaroc.model.gameinfo.GameStatus;
import game.integration_project1_zaroc.model.gameinfo.PawnColor;
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
            case 3 -> 10000;
            default -> 1000;
        };
    }

    public Turn getBestTurn(Game actualGame) {
        ZarocNode root = new ZarocNode(actualGame.gameCopy(), null, null);

        for (int i = 0; i < iterations; i++) {
            // 1. SELECT: Zoek het meest interessante blad in de huidige boom
            ZarocNode leaf = select(root);

            // 2. EXPAND: Voeg een nieuwe, nog niet geprobeerde zet toe aan dat blad
            ZarocNode newNode = expand(leaf);

            // 3. SIMULATE: "Droom" vanaf die nieuwe zet hoe het spel afloopt
            Player winner = simulate(newNode);

            // 4. BACKPROPAGATE: Vertel de hele route terug naar boven hoe goed de droom was
            backpropagate(newNode, winner);
        }

        ZarocNode bestChild = null;
        int maxVisits = -1;

        for (ZarocNode child : root.getChildren()) {
            if (child.getVisits() > maxVisits) {
                maxVisits = child.getVisits();
                bestChild = child;
            }
        }

        return (bestChild != null) ? bestChild.getInboundTurn() : null;
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
        return node;
    }

    private ZarocNode expand(ZarocNode node) {
        if (node.getState().getStatus() != GameStatus.PLAYING) return node;

        List<Turn> possibleTurns = MoveGenerator.getAllLegalTurns(node.getState());

        java.util.Collections.shuffle(possibleTurns);

        for (Turn turn : possibleTurns) {
            boolean alreadyExpanded = false;
            for (ZarocNode child : node.getChildren()) {
                if (turn.equals(child.getInboundTurn())) {
                    alreadyExpanded = true;
                    break;
                }
            }

            if (!alreadyExpanded) {
                Game nextState = node.getState().gameCopy();
                executeFullTurnOnSim(nextState, turn);
                ZarocNode newNode = new ZarocNode(nextState, node, turn);
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

        Peg s1 = game.getBoard().getPegPosition(turn.getFirstMove().getStartPeg().getYPosition(), turn.getFirstMove().getStartPeg().getXPosition());
        Peg d1 = game.getBoard().getPegPosition(turn.getFirstMove().getDestinationPeg().getYPosition(), turn.getFirstMove().getDestinationPeg().getXPosition());

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
            } else if (temp.getParent() != null) {
                Player p = temp.getParent().getState().getCurrentTurn().getCurrentPlayer();
                temp.addScore(calculateProgress(node.getState(), p));
            }

            temp = temp.getParent();
        }
    }

    private double calculateProgress(Game game, Player p) {
        if (p == null) return 0.5;

        double score = 0;
        PawnColor myColor = (game.getParticipation1().getPlayer().equals(p))
                ? game.getParticipation1().getPawnColor()
                : game.getParticipation2().getPawnColor();

        for (Peg[] row : game.getBoard().getAllPegs()) {
            for (Peg peg : row) {
                if (peg != null && !peg.getPawns().isEmpty()) {
                    for (Pawn pawn : peg.getPawns()) {
                        if (pawn.getPawnColor() == myColor) {
                            score += (peg.getYPosition() * 0.05);
                            if (peg.getYPosition() == 3) score += 0.1;
                        }
                    }
                }
            }
        }
        return Math.min(score, 0.8);
    }
}