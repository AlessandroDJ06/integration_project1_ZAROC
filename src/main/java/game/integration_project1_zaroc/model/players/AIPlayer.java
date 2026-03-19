package game.integration_project1_zaroc.model.players;

import game.integration_project1_zaroc.model.ai.AiModel;
import game.integration_project1_zaroc.model.gamelogic.Game;
import game.integration_project1_zaroc.model.gamelogic.Move;

public class AIPlayer extends Player {
    private Difficulty difficulty;
    private AiModel brain;

    public AIPlayer(Difficulty difficulty, String username) {
        super(username);
        this.difficulty = difficulty;
        this.brain = new AiModel(difficulty.ordinal());
    }
    public Move decideMove(Game game) {
        return brain.getBestMove(game);
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
