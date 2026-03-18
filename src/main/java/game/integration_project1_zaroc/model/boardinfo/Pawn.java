package game.integration_project1_zaroc.model.boardinfo;

import game.integration_project1_zaroc.model.gameinfo.PawnColor;

public class Pawn {
    private Peg currentPeg;
    private PawnColor pawnColor;

    public Pawn(Peg currentPeg, PawnColor pawnColor) {
        this.currentPeg = currentPeg;
        this.pawnColor = pawnColor;
    }

    public Pawn(Peg currentPeg){
        this.currentPeg=currentPeg;
    }

    public Peg getCurrentPeg() {
        return currentPeg;
    }

    public void setCurrentPeg(Peg currentPeg) {
        this.currentPeg = currentPeg;
    }
}
