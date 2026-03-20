package game.integration_project1_zaroc.dao;

import game.integration_project1_zaroc.model.gameinfo.Game;
import game.integration_project1_zaroc.model.gameinfo.GameStatus;


public class GamesManager {
    public Game pauseGame(Game game) {
        return updateGameStatus(game, GameStatus.PAUSED);

    }

    public Game endGame(Game game) {
        return updateGameStatus(game, GameStatus.ENDED);
    }
    public Game continiueGame(Game game)  {
        return updateGameStatus(game, GameStatus.PLAYING);
    }
    private Game updateGameStatus(Game game, GameStatus gameStatus){
    game.setStatus(gameStatus);
    return game;
    }


}
