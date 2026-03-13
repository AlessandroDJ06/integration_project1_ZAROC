package game.integration_project1_zaroc.model.selectionslider;

import game.integration_project1_zaroc.model.players.Difficulty;

public class DifficultyPickerModel {
    private final int AMOUNT_OF_DIFFICULTIES = Difficulty.values().length;
    private int currentIndex;

    public DifficultyPickerModel(){
        this.currentIndex = 0;
    }

    public void increaseCurrentIndex(){
        currentIndex = (currentIndex + 1) % AMOUNT_OF_DIFFICULTIES;
    }

    public void decreaseCurrentIndex(){
        currentIndex = (currentIndex - 1 + AMOUNT_OF_DIFFICULTIES) % AMOUNT_OF_DIFFICULTIES;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
}
