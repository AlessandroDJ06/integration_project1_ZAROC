package game.integration_project1_zaroc.model.gameinfo;

import game.integration_project1_zaroc.model.gamelogic.Turn;

import java.util.ArrayList;

public class Game {
    private GameStatus status;
    private ArrayList<Turn> turns;

    public Game() {
        this.status = GameStatus.PLAYING;
        turns = new ArrayList<>();
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }
}
