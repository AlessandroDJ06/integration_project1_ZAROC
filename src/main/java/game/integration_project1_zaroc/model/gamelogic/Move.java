package game.integration_project1_zaroc.model.gamelogic;

import game.integration_project1_zaroc.model.boardinfo.Pawn;
import game.integration_project1_zaroc.model.boardinfo.Peg;

import java.time.LocalDateTime;

public class Move {
    private MoveNumber moveNumber;
    //private Duration duration;
    private final LocalDateTime timestamp;
    private Peg startPeg;
    private Peg destinationPeg;
    private Pawn pawn;
    private Turn turn;

    public Move(MoveNumber moveNumber, Pawn pawn, Peg destinationPeg, Turn turn) {

        this.moveNumber = moveNumber;
        this.pawn = pawn;
        this.turn = turn;
        timestamp = LocalDateTime.now();
        while (!isLegal(pawn, destinationPeg)) {
            try {
                this.startPeg = pawn.getCurrentPeg();
                this.destinationPeg = destinationPeg;

            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e);
            }
        }

    }

    public boolean isLegal(Pawn pawn, Peg destinationPeg){
        boolean legalCheck = false;

        for (int row = 0; row < turn.getGame().getBoard().getAmountOfRows().length; row++) {
            int xStart = pawn.getCurrentPeg().getXPosition();
            int yStart = pawn.getCurrentPeg().getYPosition();

            int xDest = destinationPeg.getXPosition();
            int yDest = destinationPeg.getYPosition();

            boolean xLegal = xDest == xStart + 2;
            boolean yLegal = yDest == yStart + 1;

            for (int column = 0; column < turn.getGame().getBoard().getAmountOfColumns().length; column++) {
                // derde rij
                if(yDest==2) {
                    boolean diagonalCheck = (yLegal && (xDest == xStart + 1 || xDest == xStart -1)) || xLegal;
                    if(diagonalCheck){
                        legalCheck = true;
                    }
                }
                else {
                    if (xLegal || yLegal) {
                        legalCheck = true;
                    }

                }
            }
        }
        return legalCheck;
    }
    public MoveNumber getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(MoveNumber moveNumber) {
            this.moveNumber = moveNumber;
    }

   /* public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }*/

    public Pawn getPawn() {
        return pawn;
    }

    public void setPawn(Pawn pawn) {
        this.pawn = pawn;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Peg getStartPeg() {
        return startPeg;
    }

    public void setStartPeg(Peg startPeg) {
        this.startPeg = startPeg;
    }

    public Peg getDestinationPeg() {
        return destinationPeg;
    }

    public void setDestinationPeg(Pawn pawn, Peg destinationPeg) {
        if(isLegal(pawn, destinationPeg)) this.destinationPeg = destinationPeg;
    }

    public Turn getTurn() {
        return turn;
    }
}
