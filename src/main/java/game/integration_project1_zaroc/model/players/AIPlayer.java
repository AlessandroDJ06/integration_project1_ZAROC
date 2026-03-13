package game.integration_project1_zaroc.model.players;

public class AIPlayer extends Player{
    private Difficulty difficulty;

    public AIPlayer(Difficulty difficulty,String username) {
        super(username);
        this.difficulty = difficulty;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
