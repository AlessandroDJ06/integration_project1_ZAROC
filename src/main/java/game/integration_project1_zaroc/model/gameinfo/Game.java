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

    public void switchCurrentPlayer() {
        if (getStatus() == GameStatus.PLAYING) {
            Turn lastTurn = turns.get(turns.size() - 1);

            if (lastTurn.getCurrentPlayer() == getPlayer1()) {
                startNewTurn(getPlayer2());
            } else {
                startNewTurn(getPlayer1());
            }
        }
    }
    public void startNewTurn(Player player){
        Turn turn = new Turn(player,this);
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

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }
}
