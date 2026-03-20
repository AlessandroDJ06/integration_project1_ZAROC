package game.integration_project1_zaroc.dao;

import game.integration_project1_zaroc.model.players.Player;
import game.integration_project1_zaroc.model.players.PlayerStyle;

public class PlayersManager {

    public Player playerStyleAggressive(Player player){
        return updatePlayerStyle(player,PlayerStyle.AGGRESSIVE);
    }
    public Player playerStylePassive(Player player){
        return updatePlayerStyle(player,PlayerStyle.PASSIVE);
    }

    private Player updatePlayerStyle(Player player, PlayerStyle playerStyle){
        player.setPlayerStyle(playerStyle);
        return player;
    }


}
