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
    //model
    private final int ITERATIONS;
    private final int MAX_MOVES = 5;
    private final double UCB_CONSTANT_VALUE = 1.41;
    //backpropagation
    private final double WIN_SCORE = 1.0;
    private final double PROGRESS_WEIGHT = 2.0;
    private final double REACHED_FINISH_BONUS = 50.0;
    private final double HEURISTIC_MAX_SCORE = 0.9;
    private final double FULL_PEG_BONUS = 0.5;

    private final Random random = new Random();

    public AiModel(int difficulty) {
        this.ITERATIONS = switch (difficulty) {
            case 1 -> 200;
            case 2 -> 1500;
            case 3 -> 2000;
            default -> 1000;
        };
    }

    public Turn getBestTurn(Game actualGame) {
        ZarocNode root = new ZarocNode(actualGame.gameCopy(), null, null);

        for (int i = 0; i < ITERATIONS; i++) {
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
                if (child.getUCBValue(UCB_CONSTANT_VALUE) > bestValue) {
                    bestValue = child.getUCBValue(UCB_CONSTANT_VALUE);
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
                if (turn.getFirstMove() != null) {
                    nextState.executeMove(turn.getFirstMove().getStartPeg(), turn.getFirstMove().getDestinationPeg());
                }
                if (nextState.getStatus() == GameStatus.PLAYING && turn.getSecondMove() != null) {
                    nextState.executeMove(turn.getSecondMove().getStartPeg(), turn.getSecondMove().getDestinationPeg());
                }

                ZarocNode newNode = new ZarocNode(nextState, node, turn);
                node.addChild(newNode);
                return newNode;
            }
        }
        return node;
    }

    private Player simulate(ZarocNode node) {
        Game simGame = node.getState().gameCopy();
        int maxTurns = MAX_MOVES;

        while (simGame.getStatus() == GameStatus.PLAYING && maxTurns > 0) {
            List<Turn> options = MoveGenerator.getAllLegalTurns(simGame);
            if (options.isEmpty()) break;

            Turn chosenTurn;

            if (random.nextDouble() > 0.1) {
                chosenTurn = getHeuristicBestTurn(options, simGame);
            } else {
                chosenTurn = options.get(random.nextInt(options.size()));
            }

            if (chosenTurn.getFirstMove() != null) {
                simGame.executeMove(chosenTurn.getFirstMove().getStartPeg(), chosenTurn.getFirstMove().getDestinationPeg());
            }
            if (simGame.getStatus() == GameStatus.PLAYING && chosenTurn.getSecondMove() != null) {
                simGame.executeMove(chosenTurn.getSecondMove().getStartPeg(), chosenTurn.getSecondMove().getDestinationPeg());
            }

            maxTurns--;
        }
        return simGame.getWinner();
    }

    private Turn getHeuristicBestTurn(List<Turn> options, Game game) {
        Turn bestTurn = options.get(0);
        int bestScore = -999;

        Player currentPlayer = game.getCurrentTurn().getCurrentPlayer();
        PawnColor myColor = (game.getParticipation1().getPlayer().equals(currentPlayer))
                ? game.getParticipation1().getPawnColor()
                : game.getParticipation2().getPawnColor();

        for (Turn turn : options) {
            int score = getTurnForwardScore(turn, myColor);
            if (score > bestScore) {
                bestScore = score;
                bestTurn = turn;
            }
        }
        return bestTurn;
    }

    private int getTurnForwardScore(Turn turn, PawnColor myColor) {
        int score = 0;
        if (turn.getFirstMove() != null) {
            score += evaluateSingleMove(turn.getFirstMove(), myColor);
        }
        if (turn.getSecondMove() != null) {
            score += evaluateSingleMove(turn.getSecondMove(), myColor);
        }
        return score;
    }


    private int evaluateSingleMove(Move move, PawnColor myColor) {
        Peg start = move.getStartPeg();
        Peg dest = move.getDestinationPeg();

        if (start == null || start.getPawns().isEmpty()) return 0;

        PawnColor pieceColor = start.getUpperPawn().getPawnColor();
        int yDiff = dest.getYPosition() - start.getYPosition();
        if (pieceColor != myColor && dest.getYPosition() == 3) return -999999;
        if (pieceColor == myColor && dest.getYPosition() == 3) return 100000;

        int score = 0;
        int pawnsOnStart = start.getPawns().size();
        int pawnsOnDest = dest.getPawns().size();

        if (pieceColor == myColor) {
            score = (yDiff * 20);

            score += (pawnsOnDest * 5);
            if (pawnsOnDest == 2) {
                score += 50;
            }
        } else {
            if (pawnsOnStart == 3) {
                score += 200;

                if (pawnsOnDest == 0) {
                    score += 100;
                } else if (pawnsOnDest == 2) {
                    score -= 300;
                }
            } else {
                score -= 100;
            }
        }

        return score;
    }

    private void backpropagate(ZarocNode node, Player winner) {
        ZarocNode temp = node;
        while (temp != null) {
            temp.addVisit();

            if (winner != null && temp.getParent() != null) {
                Player playerWhoMoved = temp.getParent().getState().getCurrentTurn().getCurrentPlayer();
                if (winner.equals(playerWhoMoved)) {
                    temp.addScore(WIN_SCORE);
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

        double myScore = 0;
        double opponentScore = 0;

        PawnColor myColor = (game.getParticipation1().getPlayer().equals(p))
                ? game.getParticipation1().getPawnColor()
                : game.getParticipation2().getPawnColor();

        for (Peg[] row : game.getBoard().getAllPegs()) {
            for (Peg peg : row) {
                if (peg != null && !peg.getPawns().isEmpty()) {

                    for (Pawn pawn : peg.getPawns()) {
                        double pieceValue = (peg.getYPosition() * 2.0);
                        if (peg.getYPosition() == 2) pieceValue += 15.0;
                        if (peg.getYPosition() == 3) pieceValue += 100.0;
                        if (pawn.getPawnColor() == myColor) {
                            myScore += pieceValue;
                        } else {
                            opponentScore += pieceValue;
                        }
                    }

                    if (peg.getPawns().size() == 3) {
                        if (peg.getUpperPawn().getPawnColor() == myColor) {
                            myScore += 1.0;
                        } else {
                            opponentScore += 1.0;
                        }
                    }
                }
            }
        }

        // Balans opmaken
        double scoreDifference = myScore - opponentScore;

        // Zorg dat de scores netjes rond de 0.5 balanceren
        double finalScore = 0.5 + (scoreDifference / 100.0);

        // We geven hier NOOIT 1.0 of 0.0 terug, want dat reserveren we voor échte winst/verlies.
        return Math.max(0.01, Math.min(finalScore, 0.99));
    }

}