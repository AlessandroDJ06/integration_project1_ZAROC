package game.integration_project1_zaroc.model.boardinfo;

import game.integration_project1_zaroc.model.gameinfo.PawnColor;

public class Pawn {
    private Peg currentPeg;
    private PawnColor color;

    public Pawn(PawnColor color){
        this.color = color;
    }

    public PawnColor getColor() {
        return color;
    }

    public Peg getCurrentPeg() {
        return currentPeg;
    }

    public void setCurrentPeg(Peg currentPeg) {
        this.currentPeg = currentPeg;
    }
}
