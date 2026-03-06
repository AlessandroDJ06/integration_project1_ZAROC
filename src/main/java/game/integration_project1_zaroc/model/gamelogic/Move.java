package game.integration_project1_zaroc.model.gamelogic;

import game.integration_project1_zaroc.model.boardinfo.Peg;

import java.time.Duration;
import java.time.LocalDateTime;

public class Move {
    private MoveNumber moveNumber;
    private Duration duration;
    private LocalDateTime timestamp;
    private Peg startPeg;
    private Peg destinationPeg;

    public Move(int moveNumber, Duration duration, LocalDateTime timestamp, Peg startPeg, Peg destinationPeg) {
        if(moveNumber==1 && ){

        }
        this.duration = duration;
        this.timestamp = timestamp;
        this.startPeg = startPeg;
        this.destinationPeg = destinationPeg;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
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

    public void setDestinationPeg(Peg destinationPeg) {
        this.destinationPeg = destinationPeg;
    }
}
