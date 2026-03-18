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


    public Move(MoveNumber moveNumber, Pawn pawn, Peg destinationPeg) {

        this.moveNumber = moveNumber;
        this.pawn = pawn;
        timestamp = LocalDateTime.now();
        if(isLegal(pawn, destinationPeg)) {
                this.startPeg = pawn.getCurrentPeg();
                this.destinationPeg = destinationPeg;
        }
        else{
            throw new IllegalArgumentException("This move is not legal!");
        }

    }

    public static boolean isLegal(Pawn pawn, Peg destinationPeg) {

        if (destinationPeg.isFull()) {
            return false;
        }

        int xStart = pawn.getCurrentPeg().getXPosition();
        int yStart = pawn.getCurrentPeg().getYPosition();

        int xDest = destinationPeg.getXPosition();
        int yDest = destinationPeg.getYPosition();


        boolean xLegal = yDest == yStart && (xDest == xStart + 2 || xDest == xStart -2);
        boolean yLegal = yDest == yStart + 1;

        // derde rij
        if (yDest == 2) {
            boolean diagonalCheck = (yLegal && (xDest == xStart + 1 || xDest == xStart - 1)) || xLegal;
            return diagonalCheck;
        } else {
            return xLegal || (yLegal && xDest == xStart);
        }
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

}
