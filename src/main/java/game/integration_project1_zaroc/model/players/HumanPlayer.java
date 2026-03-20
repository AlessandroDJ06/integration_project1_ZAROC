package game.integration_project1_zaroc.model.players;

import game.integration_project1_zaroc.model.gameinfo.PawnColor;

public class HumanPlayer extends Player {
    private String username;
    private String email;


    public HumanPlayer(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
