package game.integration_project1_zaroc.model.ai;

import game.integration_project1_zaroc.model.gamelogic.*;
import game.integration_project1_zaroc.model.boardinfo.Peg;
import game.integration_project1_zaroc.model.players.Player;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {

    // FIX: De originele versie maakte voor elke eerste zet een volledige
    // gameCopy() om de tweede zetten te berekenen. Bij 2000 iteraties en
    // tientallen mogelijke eerste zetten is dat extreem traag en krijgt de AI
    // simpelweg geen tijd om genoeg te itereren.
    //
    // Oplossing: we maken de kopie één keer PER eerste zet (onvermijdelijk,
    // want de game-state moet bijgewerkt zijn voor de tweede zet), maar we
    // bundelen alles in één gestroomlijnde pas zonder dubbele lookups.
    public static List<Turn> getAllLegalTurns(Game game) {
        List<Turn> allPossibleTurns = new ArrayList<>();

        if (game.getCurrentTurn() == null) return allPossibleTurns;
        Player activePlayer = game.getCurrentTurn().getCurrentPlayer();

        Peg[][] allPegs = game.getBoard().getAllPegs();
        int rows = allPegs.length;

        for (int r1 = 0; r1 < rows; r1++) {
            for (int c1 = 0; c1 < allPegs[r1].length; c1++) {
                Peg start1 = allPegs[r1][c1];
                if (start1 == null || start1.getPawns().isEmpty()) continue;

                List<Move> firstMoves = game.getLegalMoves(start1);
                if (firstMoves.isEmpty()) continue;

                for (Move m1 : firstMoves) {
                    // Één kopie per eerste zet (onvermijdelijk)
                    Game tempGame = game.gameCopy();
                    Peg[][] tempPegs = tempGame.getBoard().getAllPegs();

                    Peg tStart1 = tempPegs[r1][c1];
                    int destY = m1.getDestinationPeg().getYPosition();
                    int destX = m1.getDestinationPeg().getXPosition();
                    Peg tDest1 = tempGame.getBoard().getPegPosition(destY, destX);

                    if (tStart1 == null || tDest1 == null) continue;

                    tempGame.selectStartPeg(tStart1);
                    tempGame.executeMove(tDest1);

                    // Genereer alle tweede zetten op de bijgewerkte state
                    Peg[][] tempPegs2 = tempGame.getBoard().getAllPegs();
                    for (int r2 = 0; r2 < tempPegs2.length; r2++) {
                        for (int c2 = 0; c2 < tempPegs2[r2].length; c2++) {
                            Peg start2 = tempPegs2[r2][c2];
                            if (start2 == null || start2.getPawns().isEmpty()) continue;

                            List<Move> secondMoves = tempGame.getLegalMoves(start2);
                            for (Move m2 : secondMoves) {
                                Turn turn = new Turn(activePlayer);
                                turn.addMove(m1);
                                turn.addMove(m2);
                                allPossibleTurns.add(turn);
                            }
                        }
                    }
                }
            }
        }

        return allPossibleTurns;
    }
}