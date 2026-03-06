package game.integration_project1_zaroc.model.gameinfo;

import game.integration_project1_zaroc.model.players.Player;

public class GameParticipation {
    private Player winner;
    private PawnColor pawnColor;
    private Player[] players;

    public GameParticipation() {
        players = new Player[2];

    }

    public Player getWinner(Player player) {
        return winner;
    }

    public void setWinner(Player player) {
        this.winner = winner;
    }

    public PawnColor getPawnColor(Player player) {
        return pawnColor;
    }

    public void setPawnColor(PawnColor pawnColor) {
        this.pawnColor = pawnColor;
    }
}
