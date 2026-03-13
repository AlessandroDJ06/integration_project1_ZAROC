package game.integration_project1_zaroc.model.gameinfo;

import game.integration_project1_zaroc.model.boardinfo.Board;
import game.integration_project1_zaroc.model.gamelogic.Turn;
import game.integration_project1_zaroc.model.players.Player;

import java.util.ArrayList;

public class Game {
    private GameStatus status;
    private GameParticipation[] gameParticipations;
    private ArrayList<Turn> turns;

    private Board board;


    public Game(GameParticipation gameParticipation1, GameParticipation gameParticipation2) {
        this.status = GameStatus.PLAYING;
        board = new Board();
        turns = new ArrayList<>();
        gameParticipations = new GameParticipation[]{gameParticipation1,gameParticipation2};
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


    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Player getPlayer1(){
        return this.gameParticipations[0].getPlayer();
    }

    public Player getPlayer2(){
        return this.gameParticipations[1].getPlayer();
    }
}
