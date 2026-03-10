package game.integration_project1_zaroc.model.pawncolorpicker;

import game.integration_project1_zaroc.model.gameinfo.PawnColor;

public class PawnColorPickerModel {
    private final int AMOUNT_OF_COLORS = PawnColor.values().length;
    private int currentIndex;

    public PawnColorPickerModel(){
        this.currentIndex = 0;
    }

    public void increaseCurrentIndex(){
        currentIndex = (currentIndex + 1) % AMOUNT_OF_COLORS;
    }

    public void decreaseCurrentIndex(){

        currentIndex = (currentIndex - 1 + AMOUNT_OF_COLORS) % AMOUNT_OF_COLORS;
    }


    public int getCurrentIndex(){
        return currentIndex;
    }
}
