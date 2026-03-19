package game.integration_project1_zaroc.model.gamelogic;

import game.integration_project1_zaroc.model.players.Player;


public class Turn {
    private Player currentPlayer;
    private int turnNumber = 0;
    private Move[] moves;


    public Turn(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
        moves = new Move[2];
        turnNumber = 0;
    }


    public void addMove(Move move) {
        if(move.isLegal(move.getStartPeg(),move.getDestinationPeg()))
            moves[move.getMoveNumber().getNumber() - 1] = move;
    }

    public void removeMove(Move move){
        moves[move.getMoveNumber().getNumber() - 1]=null;
    }

    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
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


