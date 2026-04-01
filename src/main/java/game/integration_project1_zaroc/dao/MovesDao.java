package game.integration_project1_zaroc.dao;
import game.integration_project1_zaroc.model.gamelogic.Move;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class MovesDao {


    public void saveMove(int turnId, Move move) throws ZarocDaoException {
        try (Connection conn = DaoUtils.createConnection()) {


            String sql = "INSERT INTO MOVES (move_number, turn_id, start_time, end_time, start_location, end_location) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, move.getMoveNumber().getNumber());
                ps.setInt(2, turnId);
                ps.setTimestamp(3, move.getStartTime());
                ps.setTimestamp(4, move.getEndTime());
                ps.setString(5, move.getStartPeg().toString());
                ps.setString(6, move.getDestinationPeg().toString());

                ps.executeUpdate();
            }

        } catch (SQLException e) {
            throw new ZarocDaoException("Kon geen move aanmaken", e);
        }
    }
}
