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


    public Move(MoveNumber moveNumber,Peg startPeg, Peg destinationPeg) {

        this.moveNumber = moveNumber;
        timestamp = LocalDateTime.now();
        if(isLegal(startPeg, destinationPeg)) {
                this.startPeg = startPeg;
                this.destinationPeg = destinationPeg;
        }
        else{
            throw new IllegalArgumentException("This move is not legal!");
        }

    }

    public static boolean isLegal(Peg startPeg, Peg destinationPeg) {
        if (destinationPeg.isFull()) return false;

        int xStart = startPeg.getXPosition();
        int yStart = startPeg.getYPosition();
        int xDest = destinationPeg.getXPosition();
        int yDest = destinationPeg.getYPosition();

        int deltaX = Math.abs(xDest - xStart);
        int deltaY = yDest - yStart;

        if (yStart == 3){
            return false;
        }
        if (deltaY == 0 && deltaX == 2) {
            return true;
        }
        if (deltaY == 1) {
            if (!startPeg.isFull()) {
                return false;
            }
            return (deltaX <= 2);
        }
        return false;
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
        if(isLegal(startPeg, destinationPeg)) this.destinationPeg = destinationPeg;
    }

}
