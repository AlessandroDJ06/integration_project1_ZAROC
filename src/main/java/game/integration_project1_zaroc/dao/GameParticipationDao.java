package game.integration_project1_zaroc.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GameParticipationDao {
    public void createGameParticipation(int gameId, int playerId, String pawnColor) throws ZarocDaoException {
        String sql = "INSERT INTO GAME_PARTICIPATION (game_id, player_id, pawn_color, winner_id) VALUES (?, ?, ?, NULL)";

        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gameId);
            ps.setInt(2, playerId);
            ps.setString(3, pawnColor);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new ZarocDaoException("Kon geen GameParticipation maken", e);
        }
    }

    public void setWinnerGameParticipation(int gameId, int winnerPlayerId) throws ZarocDaoException {
        String sql = "UPDATE GAME_PARTICIPATION SET winner_id = ? WHERE game_id = ?";

        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, winnerPlayerId);
            ps.setInt(2, gameId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new ZarocDaoException("kon geen winner zetten", e);
        }
    }
}
