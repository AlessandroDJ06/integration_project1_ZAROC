package game.integration_project1_zaroc.model.selectionslider;


public class ProfilePicturePickerModel {
    private final int AMOUNT_OF_PLAYER_PICTURES = 18;
    private int currentIndex;

    public ProfilePicturePickerModel(){
        this.currentIndex = 0;
    }

    public void increaseCurrentIndex(){
        currentIndex = (currentIndex + 1) % AMOUNT_OF_PLAYER_PICTURES;
    }

    public void decreaseCurrentIndex(){
        currentIndex = (currentIndex - 1 + AMOUNT_OF_PLAYER_PICTURES) % AMOUNT_OF_PLAYER_PICTURES;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
}
