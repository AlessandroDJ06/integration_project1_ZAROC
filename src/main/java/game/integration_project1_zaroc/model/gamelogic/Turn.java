package game.integration_project1_zaroc.model.gamelogic;

import game.integration_project1_zaroc.model.gameinfo.Game;
import game.integration_project1_zaroc.model.players.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Turn {
    private Player currentPlayer;
    private static int turnNumber = 0;
    private Move[] moves;
    private Game game;

    public Turn(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
        moves = new Move[2];
        turnNumber++;
    }

    public Game getGame() {
        return this.getGame();
    }

    public void addMove(Move move) {
        moves[move.getMoveNumber().getNumber() - 1] = move;

    }

   /* public LocalDateTime getMoveDuration(Move firstMove, Move secondMove) {

        return secondMove.getTimestamp().minusSeconds(firstMove.getTimestamp().getSecond());
    }*/
}

