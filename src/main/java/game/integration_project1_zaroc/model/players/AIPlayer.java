package game.integration_project1_zaroc.model.players;

import game.integration_project1_zaroc.model.ai.AiModel;
import game.integration_project1_zaroc.model.gamelogic.Game;
import game.integration_project1_zaroc.model.gamelogic.Turn;

public class AIPlayer extends Player {
    private Difficulty difficulty;
    private AiModel model;

    public AIPlayer(Difficulty difficulty, String username) {
        super(username);
        this.difficulty = difficulty;
        this.model = new AiModel(difficulty.ordinal(),username);
    }
    public Turn decideTurn(Game game) {
        return model.getBestTurn(game , this);
    }
    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public AiModel getModel() {
        return model;
    }
}
