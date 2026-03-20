package game.integration_project1_zaroc.dao;



import game.integration_project1_zaroc.model.gamelogic.Turn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TurnsDao {

    public int saveTurn(int gameId, Turn turn) throws ZarocDaoException {
        String sql = "INSERT INTO TURNS (turn_number, player_id, game_id) VALUES (?, ?, ?)";

        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, turn.getTurnNumber());
            ps.setInt(2, turn.getCurrentPlayer().getPlayerId());
            ps.setInt(3, gameId);

            ps.executeUpdate();

            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new ZarocDaoException("Kon geen Turn maken", e);
        }

        throw new ZarocDaoException("Turn werd toegevoegd aan de DB maar er werd geen ID teruggegeven");

    }


}
