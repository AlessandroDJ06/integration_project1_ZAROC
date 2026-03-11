package game.integration_project1_zaroc.model.pawncolorpicker;

import game.integration_project1_zaroc.model.gameinfo.PawnColor;

public class PawnColorPickerModel {
    private final int AMOUNT_OF_COLORS = PawnColor.values().length;
    private int currentIndexOtherPicker;
    private int currentIndex;

    public PawnColorPickerModel(int startIndex){
        this.currentIndex = startIndex;
        this.currentIndexOtherPicker = 0 ;
    }

    public void increaseCurrentIndex(){
        if ((currentIndex + 1) % AMOUNT_OF_COLORS == currentIndexOtherPicker){
            currentIndex = (currentIndex + 2) % AMOUNT_OF_COLORS;
        } else {
            currentIndex = (currentIndex + 1) % AMOUNT_OF_COLORS;
        }

    }

    public void decreaseCurrentIndex(){
        if ((currentIndex - 1 + AMOUNT_OF_COLORS) % AMOUNT_OF_COLORS == currentIndexOtherPicker){
            currentIndex = (currentIndex - 2 + AMOUNT_OF_COLORS) % AMOUNT_OF_COLORS;
        } else {
            currentIndex = (currentIndex - 1 + AMOUNT_OF_COLORS) % AMOUNT_OF_COLORS;
        }

    }


    public int getCurrentIndex(){
        return currentIndex;
    }

    public int getCurrentIndexOtherPicker() {
        return currentIndexOtherPicker;
    }

    public void setCurrentIndexOtherPicker(int currentIndexOtherPicker) {
        this.currentIndexOtherPicker = currentIndexOtherPicker;
    }
}
