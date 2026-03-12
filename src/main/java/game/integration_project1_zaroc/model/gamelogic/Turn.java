package game.integration_project1_zaroc.model.gamelogic;

import game.integration_project1_zaroc.model.gameinfo.Game;
import game.integration_project1_zaroc.model.gameinfo.GameParticipation;
import game.integration_project1_zaroc.model.gameinfo.GameStatus;
import game.integration_project1_zaroc.model.players.AIPlayer;
import game.integration_project1_zaroc.model.players.HumanPlayer;
import game.integration_project1_zaroc.model.players.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Turn {
    private Player currentPlayer;
    private static int turnNumber = 0;
    private Move[] moves;
    private Game game;

    public Turn(Player currentPlayer,Game game) {
        this.currentPlayer = currentPlayer;
        moves = new Move[2];
        turnNumber++;
        this.game = game;

    }


    public void addMove(Move move) {
        //if(isLegal(move)){
            moves[move.getMoveNumber().getNumber() - 1] = move;
        //}
    }

    //TODO: legal check - eerst de array van pegs volledig afwerken
    public boolean isLegal(Move move){
        boolean legalCheck = false;

        //if(move.getDestionationPeg()==move.getPawn().getCurrentPeg())
        return legalCheck;
    }

    public void undoMove(Move move) {
        int index = move.getMoveNumber().getNumber() - 1;

        moves[index].getPawn().setCurrentPeg(move.getStartPeg());
        moves[index]=null;
        if(index==0){
            game.switchCurrentPlayer();
        }
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


