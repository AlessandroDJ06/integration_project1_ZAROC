package game.integration_project1_zaroc.model.players;

import game.integration_project1_zaroc.model.gameinfo.PawnColor;

public abstract class Player {
    private PlayerStyle playerStyle;

    // in het begin is de playerstyle null;
    public Player() {
        playerStyle=null;
    }

    public Player(PlayerStyle playerStyle) {
        this.playerStyle=playerStyle;
    }

    public PlayerStyle getPlayerStyle() {
        return playerStyle;
    }

    public void setPlayerStyle(PlayerStyle playerStyle) {
        this.playerStyle = playerStyle;
    }
}
