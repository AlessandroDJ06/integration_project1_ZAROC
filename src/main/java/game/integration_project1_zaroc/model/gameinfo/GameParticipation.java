package game.integration_project1_zaroc.model.gameinfo;

import game.integration_project1_zaroc.model.players.AIPlayer;
import game.integration_project1_zaroc.model.players.HumanPlayer;
import game.integration_project1_zaroc.model.players.Player;

import java.util.ArrayList;

public class GameParticipation {
    private Player winner;
    private PawnColor humanPawnColor;
    private PawnColor aiPawnColor;
    private Player[] players;

    public GameParticipation(HumanPlayer humanPlayer,PawnColor humanPawnColor, AIPlayer aiPlayer,  PawnColor aiPawnColor) {
        this.humanPawnColor=humanPawnColor;
        this.aiPawnColor=aiPawnColor;
        players = new Player[]{humanPlayer, aiPlayer};

    }
    public GameParticipation(HumanPlayer humanPlayer, AIPlayer aiPlayer){
        this(humanPlayer,PawnColor.WHITE, aiPlayer, PawnColor.BLACK);
    }

    public Player getWinner() {
        return winner;
    }

    public PawnColor getHumanPawnColor() {
        return humanPawnColor;
    }

    public void setHumanPawnColor(PawnColor humanPawnColor) {
        this.humanPawnColor = humanPawnColor;
    }

    public PawnColor getAiPawnColor() {
        return aiPawnColor;
    }

    public void setAiPawnColor(PawnColor aiPawnColor) {
        this.aiPawnColor = aiPawnColor;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }
}
