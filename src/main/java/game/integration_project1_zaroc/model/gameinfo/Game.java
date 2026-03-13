package game.integration_project1_zaroc.model.gameinfo;

import game.integration_project1_zaroc.model.boardinfo.Board;
import game.integration_project1_zaroc.model.gamelogic.Turn;
import game.integration_project1_zaroc.model.players.Player;

import java.util.ArrayList;

public class Game {
    private GameStatus status;
    private GameParticipation[] gameParticipations;
    private ArrayList<Turn> turns;
    private Player player1;
    private Player player2;
    private Board board;


    public Game(GameParticipation gameParticipation1, GameParticipation gameParticipation2) {
        this.status = GameStatus.PLAYING;
        this.player1=gameParticipation1.getPlayer();
        this.player2=gameParticipation2.getPlayer();
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

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
