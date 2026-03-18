package game.integration_project1_zaroc.model.boardinfo;

import game.integration_project1_zaroc.model.boardinfo.layers.LayerLevel;
import game.integration_project1_zaroc.model.boardinfo.layers.MaxCapacity;

import java.util.ArrayList;

public class Peg {
    private int xPosition;
    private int yPosition;
    private LayerLevel layerLevel;
    private MaxCapacity maxCapacity;
    private ArrayList<Pawn> pawns;
    private int pawnCount = 0;

    public Peg(int xPosition, int yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.layerLevel = LayerLevel.values()[yPosition];
        this.maxCapacity = MaxCapacity.values()[layerLevel.ordinal()];
        pawns = new ArrayList<>();
    }

    public void addPawnToPeg(Pawn pawn) {
        if (!isFull()) {
            pawn.setCurrentPeg(this);
            pawns.add(pawn);
            pawnCount++;
        }
    }
    public void removePawnFromPeg(Pawn pawn){
        pawns.remove(pawn);
        pawnCount--;
    }

    public ArrayList<Pawn> getPawns() {
        return pawns;
    }

    public int getPawnCount() {
        return pawnCount;
    }

    public int getXPosition() {
        return xPosition;
    }

    public void setXPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    public void setYPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    public LayerLevel getLayerLevel() {
        return layerLevel;
    }

    public void setLayerLevel(LayerLevel layerLevel) {
        this.layerLevel = layerLevel;
    }

    public MaxCapacity getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(MaxCapacity maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public boolean isFull() {
        return this.pawnCount >= this.maxCapacity.getMaxCapacityNumber();
    }
}
