package game.integration_project1_zaroc.model.gameinfo;

import game.integration_project1_zaroc.model.players.Player;


public class GameParticipation {
    private boolean winner;
    private PawnColor chosenPawnColor;
    private Player player;


    public GameParticipation(Player player,PawnColor chosenPawnColor) {
        this.player=player;
        this.chosenPawnColor = chosenPawnColor;
        this.winner = false;
    }

    public boolean getWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public PawnColor getChosenPawnColor() {
        return chosenPawnColor;
    }

    public void setChosenPawnColor(PawnColor chosenPawnColor) {
        this.chosenPawnColor = chosenPawnColor;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

}
