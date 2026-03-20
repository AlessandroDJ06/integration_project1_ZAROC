package game.integration_project1_zaroc.dao;


import game.integration_project1_zaroc.model.gameinfo.Game;
import game.integration_project1_zaroc.model.gameinfo.GameParticipation;
import game.integration_project1_zaroc.model.players.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class GameParticipationDao {
    public void createGameParticipation(Game game) throws ZarocDaoException {
        GameParticipation[] gameParticipations = game.getGameParticipations();
        String sql = "INSERT INTO GAME_PARTICIPATION (game_id, player_id, pawn_color, winner_id) VALUES (?, ?, ?, NULL)";

        for(GameParticipation gameParticipation: gameParticipations){
        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, game.getGameId());
            ps.setInt(2, gameParticipation.getPlayer().getPlayerId());
            ps.setString(3, gameParticipation.getPawnColor().toString());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new ZarocDaoException("Kon geen GameParticipation maken", e);
        }
        }
    }

    public void updateGameParticipationWinner(Player player,Game game) throws ZarocDaoException { //Player is winning player. //geen idee hoe dit ooit zou werken als je met boolean wilt werken dat is veel moeilijker dan id en even gemakkelijk om op te fetchen van db.
        String sql = "UPDATE GAME_PARTICIPATION SET winner_id = ? WHERE game_id = ?";

        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, player.getPlayerId());
            ps.setInt(2, game.getGameId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new ZarocDaoException("kon geen winner zetten", e);
        }
    }
}
