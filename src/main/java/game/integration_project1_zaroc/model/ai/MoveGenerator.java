package game.integration_project1_zaroc.model.ai;

import game.integration_project1_zaroc.model.gamelogic.*;
import game.integration_project1_zaroc.model.boardinfo.Peg;
import game.integration_project1_zaroc.model.players.Player;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {
    public static List<Turn> getAllLegalTurns(Game game) {
        List<Turn> allPossibleTurns = new ArrayList<>();

        if (game.getCurrentTurn() == null) return allPossibleTurns;
        Player activePlayer = game.getCurrentTurn().getCurrentPlayer();

        for (int r1 = 0; r1 < 4; r1++) {
            for (int c1 = 0; c1 < 10; c1++) {
                Peg start1 = game.getBoard().getPegPosition(r1, c1);

                if (start1 == null || start1.getPawns().isEmpty()) continue;

                List<Move> firstMoves = game.getLegalMoves(start1);

                for (Move m1 : firstMoves) {
                    Game tempGame = game.gameCopy();

                    Peg tStart1 = tempGame.getBoard().getPegPosition(r1, c1);
                    Peg tDest1 = tempGame.getBoard().getPegPosition(
                            m1.getDestinationPeg().getYPosition(),
                            m1.getDestinationPeg().getXPosition()
                    );

                    if (tStart1 != null && tDest1 != null) {
                        tempGame.selectStartPeg(tStart1);
                        tempGame.executeMove(tDest1);
                    } else {
                        continue;
                    }

                    for (int r2 = 0; r2 < 4; r2++) {
                        for (int c2 = 0; c2 < 10; c2++) {
                            Peg start2 = tempGame.getBoard().getPegPosition(r2, c2);
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