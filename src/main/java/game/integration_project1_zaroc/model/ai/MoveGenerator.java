package game.integration_project1_zaroc.model.ai;

import game.integration_project1_zaroc.model.gamelogic.*;
import game.integration_project1_zaroc.model.boardinfo.Pawn;
import game.integration_project1_zaroc.model.boardinfo.Peg;
import game.integration_project1_zaroc.model.gameinfo.PawnColor;
import game.integration_project1_zaroc.model.players.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for generating all legal moves and turns for the AI players.
 * It handles the logic for both single moves and two-part turns.
 *
 * v1.3 FIX: The generator previously only considered pegs where the active player's
 * pawn was on TOP. This caused it to completely ignore moves where the active player's
 * pawn is buried under opponent pawns — which is a legal and often critical move
 * (moving the entire stack including captured opponent pawns).
 *
 * Fix: a peg is now eligible as a move source if the active color appears ANYWHERE
 * in the stack. The game's own getLegalMoves() is the authority on legality —
 * we just need to make sure we ask it about every relevant peg.
 *
 * @author Alessandro De Jongh
 * @version 1.3
 */
public class MoveGenerator {

    /**
     * Returns true if the active player's color appears anywhere in this peg's stack.
     * This covers both the case where they are on top AND the case where they are
     * underneath captured opponent pawns.
     */
    private static boolean stackContainsColor(Peg peg, PawnColor color) {
        for (Pawn p : peg.getPawns()) {
            if (p.getPawnColor().equals(color)) return true;
        }
        return false;
    }

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

                // v1.3 FIX: was `!start1.getUpperPawn().getPawnColor().equals(activeColor)`
                // which skipped any peg where an opponent pawn was on top.
                // Now we include any peg that contains our color anywhere in the stack.
                if (start1 == null || start1.getPawns().isEmpty() ||
                        !stackContainsColor(start1, activeColor)) {
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

                    boolean foundSecondMove = false;
                    Peg[][] tempPegs2 = tempGame.getBoard().getAllPegs();

                    for (int r2 = 0; r2 < tempPegs2.length; r2++) {
                        for (int c2 = 0; c2 < tempPegs2[r2].length; c2++) {
                            Peg start2 = tempPegs2[r2][c2];

                            // Same fix applied to the second move search
                            if (start2 == null || start2.getPawns().isEmpty() ||
                                    !stackContainsColor(start2, activeColor)) {
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