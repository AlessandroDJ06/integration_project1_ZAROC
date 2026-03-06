package game.integration_project1_zaroc.model.gamelogic;

import game.integration_project1_zaroc.model.boardinfo.Peg;
import game.integration_project1_zaroc.model.gameinfo.GameStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class Move {
    private MoveNumber moveNumber;
    private Duration duration;
    private final LocalDateTime timestamp;
    private Peg startPeg;
    private Peg destinationPeg;
    private Turn turn;

    public Move(MoveNumber moveNumber, Peg startPeg, Peg destinationPeg) {
        this.moveNumber = moveNumber;
        timestamp = LocalDateTime.now();
        this.startPeg = startPeg;
        this.destinationPeg = destinationPeg;
    }

    public MoveNumber getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(int moveNumber) {
        if(moveNumber == 1 || moveNumber == 2){
            this.moveNumber = MoveNumber.values()[moveNumber-1];
        }
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
