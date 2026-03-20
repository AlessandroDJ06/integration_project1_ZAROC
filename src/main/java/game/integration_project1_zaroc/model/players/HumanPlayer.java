package game.integration_project1_zaroc.model.players;

import game.integration_project1_zaroc.model.gameinfo.PawnColor;

public class HumanPlayer extends Player {
    private String email;


    public HumanPlayer(String username, String email) {
        super(username);
        this.email = email;
    }

    public void setUsername(String username) {
        super.setUsername(username);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
