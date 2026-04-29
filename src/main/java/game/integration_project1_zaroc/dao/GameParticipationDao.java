package game.integration_project1_zaroc.dao;


import game.integration_project1_zaroc.model.gameinfo.GameParticipation;
import game.integration_project1_zaroc.model.gamelogic.Game;
import game.integration_project1_zaroc.model.players.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class GameParticipationDao {
    public void createGameParticipation(Game game) throws ZarocDaoException {
        GameParticipation[] gameParticipations = game.getGameParticipations();
        String sql = "INSERT INTO GAME_PARTICIPATION (game_id, player_id, pawn_color, winner) VALUES (?, ?, ?, ?)";

        for(int i = 0 ; i < gameParticipations.length ; i++){
        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, game.getGameId());
            ps.setInt(2, gameParticipations[i].getPlayer().getPlayerId());
            ps.setString(3, gameParticipations[i].getChosenPawnColor().toString());
            ps.setBoolean(4,gameParticipations[i].getWinner());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new ZarocDaoException("Kon geen GameParticipation maken", e);
        }
        }
    }

    public void updateGameParticipationWinner(Game game,GameParticipation gameParticipation) throws ZarocDaoException {
        String sql = "UPDATE GAME_PARTICIPATION SET winner = ? WHERE game_id = ? AND player_id = ?";

        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, gameParticipation.getWinner());
            ps.setInt(2, game.getGameId());
            ps.setInt(3, gameParticipation.getPlayer().getPlayerId());


            ps.executeUpdate();

        } catch (SQLException e) {
            throw new ZarocDaoException("kon geen winner zetten", e);
        }
    }
}
