package game.integration_project1_zaroc.model.gameinfo;

import game.integration_project1_zaroc.model.players.Player;


public class GameParticipation {
    private boolean winner;
    private PawnColor pawnColor;
    private Player player;

    public GameParticipation(Player player,PawnColor pawnColor) {
        this.player=player;
        this.pawnColor= pawnColor;
        this.winner = false;
    }

    public boolean getWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public PawnColor getPawnColor() {
        return pawnColor;
    }

    public void setPawnColor(PawnColor pawnColor) {
        this.pawnColor = pawnColor;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

}
