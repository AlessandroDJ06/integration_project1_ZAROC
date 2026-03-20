package game.integration_project1_zaroc.model.gameinfo;

public enum GameStatus {
    PLAYING,PAUSED,ENDED;
    @Override
    public String toString() {
        return switch (this) {
            case PLAYING -> "Playing";
            case PAUSED -> "Paused";
            case ENDED -> "Ended";
        };
    }
}
