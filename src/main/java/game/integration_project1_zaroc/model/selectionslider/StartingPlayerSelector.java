package game.integration_project1_zaroc.model.selectionslider;

import game.integration_project1_zaroc.model.players.Player;

public class StartingPlayerSelector {
    private final int AMOUNT_OF_PLAYERS = 2;
    private Player[] players;
    private int currentIndex;

    public StartingPlayerSelector(){
        this.currentIndex = 0;
        this.players = new Player[AMOUNT_OF_PLAYERS];
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

    public void setPlayer1(Player player){
        this.players[0] = player;
    }

    public void setPlayer2(Player player){
        this.players[1] = player;
    }

    public Player[] getPlayers() {
        return players;
    }
}
