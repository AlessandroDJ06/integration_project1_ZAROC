package game.integration_project1_zaroc.model.gameinfo;

import game.integration_project1_zaroc.model.players.Player;


public class GameParticipation {
    private Player winner;
    private PawnColor pawnColor;
    private Player player;

    public GameParticipation(Player player,PawnColor pawnColor) {
        this.player=player;
        this.pawnColor= pawnColor;
        this.winner =null;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
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
