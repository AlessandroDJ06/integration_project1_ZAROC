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

    public Peg(int xPosition, int yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.layerLevel= LayerLevel.values()[yPosition];
        this.maxCapacity = MaxCapacity.values()[layerLevel.ordinal()];
        pawns = new ArrayList<>();
    }

    public void addPawnToPeg(Pawn pawn, Peg currentPeg){
        pawn.setCurrentPeg(currentPeg);
        pawns.add(pawn);
    }
    public void removePawnFromPeg(Pawn pawn, Peg currentPeg){
        pawn.setCurrentPeg(null);
        pawns.remove(pawn);
    }

    @Override
    public String toString() {
        return xPosition+","+yPosition;
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
}
