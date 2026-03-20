package game.integration_project1_zaroc.dao;


import game.integration_project1_zaroc.model.gameinfo.Game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GamesDao {
    public int createGame(Game game) throws ZarocDaoException {
        String sql = "INSERT INTO GAMES (game_status) VALUES (?)";

        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, "PLAYING");
            ps.executeUpdate();

            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new ZarocDaoException("Kon geen nieuwe game maken", e);
        }
        throw new ZarocDaoException("Game werd in de DB toegevoegd maar er werd geen ID teruggegeven");
    }

    public void updateGame(Game game) throws ZarocDaoException {
        String sql = "UPDATE GAMES SET game_status = ? WHERE game_id = ?";

        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,game.getStatus().toString());
            ps.setInt(2,game.getGameId());

        } catch (SQLException sqlException){
                throw new ZarocDaoException("Kon de game niet updaten.", sqlException);
        }


    }
}
