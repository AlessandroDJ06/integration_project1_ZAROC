package game.integration_project1_zaroc.model.gameinfo;

import game.integration_project1_zaroc.model.boardinfo.Pawn;
import game.integration_project1_zaroc.model.gamelogic.Turn;
import game.integration_project1_zaroc.model.players.Player;

import java.util.ArrayList;

public class Game {
    private GameStatus status;
    private GameParticipation[] gameParticipations;
    private ArrayList<Turn> turns;
    private Player player1;
    private Player player2;

    public Game(Player player1, Player player2) {
        this.status = GameStatus.PLAYING;
        this.player1=player1;
        this.player2=player2;
        turns = new ArrayList<>();
        gameParticipations = new GameParticipation[]{new GameParticipation(player1,PawnColor.WHITE),new GameParticipation(player2,PawnColor.BLACK)};
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public void startNewTurn(Turn turn){
        turns.add(turn);
    }

}
