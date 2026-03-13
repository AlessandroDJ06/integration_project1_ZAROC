package game.integration_project1_zaroc.model.gamelogic;

import game.integration_project1_zaroc.model.boardinfo.Pawn;
import game.integration_project1_zaroc.model.boardinfo.Peg;
import game.integration_project1_zaroc.model.gameinfo.GameStatus;

import java.time.Duration;
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
        this.startPeg = pawn.getCurrentPeg();
        this.destinationPeg = destinationPeg;
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

    public void setDestinationPeg(Peg destinationPeg) {
        this.destinationPeg = destinationPeg;
    }
}
