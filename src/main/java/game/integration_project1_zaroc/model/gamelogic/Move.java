package game.integration_project1_zaroc.model.gamelogic;

import game.integration_project1_zaroc.model.boardinfo.Pawn;
import game.integration_project1_zaroc.model.boardinfo.Peg;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

public class Move {
    private MoveNumber moveNumber;
    //private Duration duration;
    private Timestamp startTime;
    private Timestamp endTime;
    private Peg startPeg;
    private Peg destinationPeg;


    public Move(MoveNumber moveNumber,Peg startPeg, Peg destinationPeg) {

        this.moveNumber = moveNumber;
        startTime = Timestamp.from(Instant.now());
        this.startPeg = startPeg;
        this.destinationPeg = destinationPeg;
        this.endTime = null;
    }

    public static boolean isLegal(Peg startPeg, Peg destinationPeg) {
        if (startPeg == null || destinationPeg == null) {
            return false;
        }
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


    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
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

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return startPeg.getXPosition() == move.startPeg.getXPosition() &&
                startPeg.getYPosition() == move.startPeg.getYPosition() &&
                destinationPeg.getXPosition() == move.destinationPeg.getXPosition() &&
                destinationPeg.getYPosition() == move.destinationPeg.getYPosition();
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(startPeg.getXPosition(), startPeg.getYPosition(),
                destinationPeg.getXPosition(), destinationPeg.getYPosition());
    }



}
