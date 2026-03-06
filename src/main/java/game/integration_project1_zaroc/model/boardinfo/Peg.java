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

    public Peg(int xPosition, int yPosition, LayerLevel layerLevel) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.layerLevel=layerLevel;
        intialiseMaxCapacity();
        pawns = new ArrayList<>();
    }

    public void intialiseMaxCapacity() {
        switch(getYPosition()){
            case 0 -> this.maxCapacity=MaxCapacity.FIRST_ROW;
            case 1 -> this.maxCapacity=MaxCapacity.SECOND_ROW;
            case 2 -> this.maxCapacity=MaxCapacity.THIRD_ROW;
            case 3 -> this.maxCapacity=MaxCapacity.FOURTH_ROW;
        }
    }

    public void addPawnToPeg(Pawn pawn, Peg destinationPeg){
        pawn.setCurrentPeg(destinationPeg);
        pawns.add(pawn);
    }
    public void removePawnFromPeg(Pawn pawn, Peg currentPeg){
        pawn.setCurrentPeg(null);
        pawns.remove(pawn);
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
}
