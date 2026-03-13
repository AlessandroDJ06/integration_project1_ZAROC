package game.integration_project1_zaroc.model.players;


public abstract class Player {
    private PlayerStyle playerStyle;
    private String username;

    // in het begin is de playerstyle null;
    public Player(String username) {
        this.username = username;
        playerStyle=null;
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

}
