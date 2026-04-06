package game.integration_project1_zaroc.model.players;

import game.integration_project1_zaroc.model.gameinfo.PawnColor;

public abstract class Player {
    private PlayerStyle playerStyle;
    private String username;
    private int playerId;
    private String profilePicture;

    // in het begin is de playerstyle null;
    public Player(String username) {
        this.username = username;
        playerStyle=null;
        playerId=-1;
        profilePicture = "EMPTY";
    }

    public Player(PlayerStyle playerStyle,String username) {
        this(username);
        this.playerStyle=playerStyle;
    }

    public PlayerStyle getPlayerStyle() {
        return playerStyle;
    }

    public void setPlayerStyle(PlayerStyle playerStyle) {
        this.playerStyle = playerStyle;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }


    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
