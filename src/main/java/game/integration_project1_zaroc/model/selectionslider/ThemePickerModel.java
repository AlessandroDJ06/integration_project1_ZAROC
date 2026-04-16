package game.integration_project1_zaroc.model.selectionslider;

public class ThemePickerModel {
private final int AMOUNT_OF_THEMES = 4;
private int currentIndex;

public ThemePickerModel(){
    this.currentIndex = 0;
}

    public void increaseCurrentIndex(){
        currentIndex = (currentIndex + 1) % AMOUNT_OF_THEMES;
    }
    public void decreaseCurrentIndex(){
    currentIndex = (currentIndex - 1 + AMOUNT_OF_THEMES) % AMOUNT_OF_THEMES;
    }
    public int getCurrentIndex() {
        return currentIndex;
    }
    public void setCurrentIndex(int index){currentIndex = index;}


}
