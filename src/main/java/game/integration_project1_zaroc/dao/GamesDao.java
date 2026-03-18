package game.integration_project1_zaroc.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GamesDao {
    public int createGame() throws ZarocDaoException {
        String sql = "INSERT INTO GAMES (game_status) VALUES (?)";

        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, "PLAYING");
            ps.executeUpdate();

            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new ZarocDaoException("Kon geen nieuwe game make", e);
        }

        throw new ZarocDaoException("Game werd in de DB toegevoegd maar er werd geen ID teruggegeven");
    }
    public void pauseGame(int gameId) throws ZarocDaoException {
        updateGameStatus(gameId, "PAUSED");
    }

    public void endGame(int gameId) throws ZarocDaoException {
        updateGameStatus(gameId, "ENDED");
    }

    private void updateGameStatus(int gameId, String status) throws ZarocDaoException {
        String sql = "UPDATE GAMES SET game_status = ? WHERE game_id = ?";

        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, gameId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new ZarocDaoException("Kon game status niet updaten", e);
        }
    }
}
