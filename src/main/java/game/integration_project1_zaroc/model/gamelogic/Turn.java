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

    public Turn(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
        moves = new Move[2];
        turnNumber++;
    }


    public void addMove(Move move) {
        moves[move.getMoveNumber().getNumber() - 1] = move;

    }

    public static int getTurnNumber() {
        return turnNumber;
    }

    /*public Move[] getMoves() {
        return moves;
    }*/
    public Move getFirstMove(){
        return moves[0];
    }

    public Move getSecondMove(){
        return moves[1];
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    /* public LocalDateTime getMoveDuration(Move firstMove, Move secondMove) {

        return secondMove.getTimestamp().minusSeconds(firstMove.getTimestamp().getSecond());
    }*/
}

