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
    private final int ITERATIONS;
    private final int MAX_MOVES = 20;
    private final double UCB_CONSTANT_VALUE = 1.41;

    private final double WIN_SCORE = 1.0;

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
            ZarocNode leaf = select(root);
            ZarocNode newNode = expand(leaf);
            double score = simulate(newNode);
            backpropagate(newNode, score);
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
                applyTurn(nextState, turn);
                ZarocNode newNode = new ZarocNode(nextState, node, turn);
                node.addChild(newNode);
                return newNode;
            }
        }
        return node;
    }

    private double simulate(ZarocNode node) {
        Game simGame = node.getState().gameCopy();
        int maxTurns = MAX_MOVES;

        // Sla de speler op die VOOR de simulatie aan de beurt was
        // (= de speler die de zet naar dit blad heeft gedaan)
        Player movingPlayer = (node.getParent() != null)
                ? node.getParent().getState().getCurrentTurn().getCurrentPlayer()
                : simGame.getCurrentTurn().getCurrentPlayer();

        while (simGame.getStatus() == GameStatus.PLAYING && maxTurns > 0) {
            List<Turn> options = MoveGenerator.getAllLegalTurns(simGame);
            if (options.isEmpty()) break;

            Turn chosenTurn;
            if (random.nextDouble() > 0.15) {
                chosenTurn = getHeuristicBestTurn(options, simGame);
            } else {
                chosenTurn = options.get(random.nextInt(options.size()));
            }

            applyTurn(simGame, chosenTurn);
            maxTurns--;
        }

        if (simGame.getStatus() != GameStatus.PLAYING) {
            Player winner = simGame.getWinner();
            return (winner != null && winner.equals(movingPlayer)) ? WIN_SCORE : 0.0;
        }

        return calculateProgress(simGame, movingPlayer);
    }

    // Helper: past een volledige turn toe op een game (eerste + tweede zet)
    private void applyTurn(Game game, Turn turn) {
        if (turn.getFirstMove() != null) {
            game.selectStartPeg(turn.getFirstMove().getStartPeg());
            game.executeMove(turn.getFirstMove().getDestinationPeg());
        }
        if (game.getStatus() == GameStatus.PLAYING && turn.getSecondMove() != null) {
            game.selectStartPeg(turn.getSecondMove().getStartPeg());
            game.executeMove(turn.getSecondMove().getDestinationPeg());
        }
    }

    private Turn getHeuristicBestTurn(List<Turn> options, Game game) {
        Turn bestTurn = options.get(0);
        int bestScore = Integer.MIN_VALUE;

        Player currentPlayer = game.getCurrentTurn().getCurrentPlayer();
        PawnColor myColor = (game.getParticipation1().getPlayer().equals(currentPlayer))
                ? game.getParticipation1().getChosenPawnColor()
                : game.getParticipation2().getChosenPawnColor();

        for (Turn turn : options) {
            int score = getTurnScore(turn, myColor);
            if (score > bestScore) {
                bestScore = score;
                bestTurn = turn;
            }
        }
        return bestTurn;
    }

    private int getTurnScore(Turn turn, PawnColor myColor) {
        int score = 0;
        if (turn.getFirstMove() != null) score += evaluateSingleMove(turn.getFirstMove(), myColor);
        if (turn.getSecondMove() != null) score += evaluateSingleMove(turn.getSecondMove(), myColor);
        return score;
    }

    private int evaluateSingleMove(Move move, PawnColor myColor) {
        Peg start = move.getStartPeg();
        Peg dest = move.getDestinationPeg();

        if (start == null || start.getPawns().isEmpty()) return 0;

        PawnColor pieceColor = start.getUpperPawn().getPawnColor();
        int yStart = start.getYPosition();
        int yDest = dest.getYPosition();
        int yDiff = yDest - yStart;

        int pawnsOnDest = dest.getPawns().size();

        if (pieceColor == myColor) {
            if (yDest == 3) return 1_000_000;

            int score = yDiff * 30;

            score += pawnsOnDest * 10;
            if (pawnsOnDest == 2) score += 60;

            if (yDest == 2) score += 80;

            return score;
        } else {

            if (yStart == 2 && yDest < yStart) return 500;

            if (yDest == 3) return -1_000_000;

            if (yDiff < 0) return 100 + (-yDiff * 20);

            if (pawnsOnDest == 0) return 50;

            if (pawnsOnDest == 2) return -200;

            return -50;
        }
    }

    private void backpropagate(ZarocNode node, double simulationScore) {
        ZarocNode temp = node;
        while (temp != null) {
            temp.addVisit();
            temp.addScore(simulationScore);
            temp = temp.getParent();
        }
    }

    private double calculateProgress(Game game, Player p) {
        if (p == null) return 0.5;

        double myScore = 0;
        double opponentScore = 0;

        PawnColor myColor = (game.getParticipation1().getPlayer().equals(p))
                ? game.getParticipation1().getChosenPawnColor()
                : game.getParticipation2().getChosenPawnColor();

        for (Peg[] row : game.getBoard().getAllPegs()) {
            for (Peg peg : row) {
                if (peg == null || peg.getPawns().isEmpty()) continue;

                for (Pawn pawn : peg.getPawns()) {
                    // y=3 is de winnende positie → enorm gewicht
                    double pieceValue = switch (peg.getYPosition()) {
                        case 3 -> 200.0;
                        case 2 -> 30.0;
                        case 1 -> 8.0;
                        default -> 1.0;
                    };

                    if (pawn.getPawnColor() == myColor) {
                        myScore += pieceValue;
                    } else {
                        opponentScore += pieceValue;
                    }
                }

                // Extra bonus voor volledige stapel op hoge positie
                if (peg.getPawns().size() == 3) {
                    double stackBonus = (peg.getYPosition() + 1) * 5.0;
                    if (peg.getUpperPawn().getPawnColor() == myColor) {
                        myScore += stackBonus;
                    } else {
                        opponentScore += stackBonus;
                    }
                }
            }
        }

        double scoreDiff = myScore - opponentScore;
        double finalScore = 0.5 + (scoreDiff / 300.0);
        return Math.max(0.01, Math.min(finalScore, 0.99));
    }
}