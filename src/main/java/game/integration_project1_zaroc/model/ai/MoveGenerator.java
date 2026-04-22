package game.integration_project1_zaroc.model.ai;

import game.integration_project1_zaroc.model.gameinfo.GameStatus;
import game.integration_project1_zaroc.model.gamelogic.*;
import game.integration_project1_zaroc.model.boardinfo.Peg;
import game.integration_project1_zaroc.model.gameinfo.PawnColor;
import game.integration_project1_zaroc.model.players.Player;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {

    /**
     * Generates a list of all legal turns currently available for the active player.
     * A turn consists of one or two consecutive moves. This method uses a deep copy
     * of the game state to simulate the outcome of the first move before generating the second.
     *
     * @param game The current {@link Game} state.
     * @return A {@link List} of all possible legal {@link Turn} objects.
     */
    public static List<Turn> getAllLegalTurns(Game game) {
        List<Turn> allPossibleTurns = new ArrayList<>();

        if (game.getCurrentTurn() == null) return allPossibleTurns;
        Player activePlayer = game.getCurrentTurn().getCurrentPlayer();

        PawnColor activeColor = (game.getParticipation1().getPlayer().getUsername().equals(activePlayer.getUsername()))
                ? game.getParticipation1().getChosenPawnColor()
                : game.getParticipation2().getChosenPawnColor();

        Peg[][] allPegs = game.getBoard().getAllPegs();

        for (int r1 = 0; r1 < allPegs.length; r1++) {
            for (int c1 = 0; c1 < allPegs[r1].length; c1++) {
                Peg start1 = allPegs[r1][c1];

                if (start1 == null || start1.getPawns().isEmpty()) {
                    continue;
                }

                List<Move> firstMoves = game.getLegalMoves(start1);
                if (firstMoves.isEmpty()) continue;

                for (Move m1 : firstMoves) {
                    Game tempGame = game.gameCopy();

                    Peg tStart1 = tempGame.getBoard().getPegPosition(r1, c1);
                    Peg tDest1 = tempGame.getBoard().getPegPosition(
                            m1.getDestinationPeg().getYPosition(),
                            m1.getDestinationPeg().getXPosition());

                    if (tStart1 == null || tDest1 == null) continue;

                    tempGame.selectStartPeg(tStart1);
                    tempGame.executeMove(tDest1);

                    if (tempGame.getStatus() == GameStatus.ENDED) {
                        Turn winningTurn = new Turn(activePlayer);
                        winningTurn.addMove(m1);
                        allPossibleTurns.add(winningTurn);
                        continue;
                    }

                    boolean foundSecondMove = false;
                    Peg[][] tempPegs2 = tempGame.getBoard().getAllPegs();

                    for (int r2 = 0; r2 < tempPegs2.length; r2++) {
                        for (int c2 = 0; c2 < tempPegs2[r2].length; c2++) {
                            Peg start2 = tempPegs2[r2][c2];

                            if (start2 == null || start2.getPawns().isEmpty()) {
                                continue;
                            }

                            List<Move> secondMoves = tempGame.getLegalMoves(start2);
                            for (Move m2 : secondMoves) {
                                foundSecondMove = true;
                                Turn turn = new Turn(activePlayer);
                                turn.addMove(m1);
                                turn.addMove(m2);
                                allPossibleTurns.add(turn);
                            }
                        }
                    }

                    if (!foundSecondMove) {
                        Turn singleMoveTurn = new Turn(activePlayer);
                        singleMoveTurn.addMove(m1);
                        allPossibleTurns.add(singleMoveTurn);
                    }
                }
            }
        }
        return allPossibleTurns;
    }
}