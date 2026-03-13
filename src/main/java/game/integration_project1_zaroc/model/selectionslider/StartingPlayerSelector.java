package game.integration_project1_zaroc.model.selectionslider;

public class StartingPlayerSelector {
    private final int AMOUNT_OF_PLAYERS = 2;
    private int currentIndex;

    public StartingPlayerSelector(){
        this.currentIndex = 0;
    }

    public void increaseCurrentIndex(){
        currentIndex = (currentIndex + 1) % AMOUNT_OF_PLAYERS;
    }

    public void decreaseCurrentIndex(){
        currentIndex = (currentIndex - 1 + AMOUNT_OF_PLAYERS) % AMOUNT_OF_PLAYERS;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
}
