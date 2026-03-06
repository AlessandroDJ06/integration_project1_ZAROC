package game.integration_project1_zaroc.model.gamelogic;

import game.integration_project1_zaroc.model.players.Player;

public class Turn {
    private Player currentPlayer;
    private static int turnNumber = 0;

    public Turn(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
        turnNumber++;
    }
}
