package game.integration_project1_zaroc.model.gamelogic;

import game.integration_project1_zaroc.model.gameinfo.Game;

import game.integration_project1_zaroc.model.players.Player;


public class Turn {
    private Player currentPlayer;
    private int turnNumber;
    private Move[] moves;
    private Game game;
    private int turnId;


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

    public int getTurnNumber() {
        return this.turnNumber;
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

    public int getTurnId() {
        return turnId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Turn turn = (Turn) o;

        boolean firstMatch = java.util.Objects.equals(((Turn) o).getFirstMove(), this.getFirstMove());
        boolean secondMatch = java.util.Objects.equals(((Turn) o).getSecondMove(), this.getSecondMove());

        return firstMatch && secondMatch;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(getFirstMove(),getSecondMove());
    }
}


