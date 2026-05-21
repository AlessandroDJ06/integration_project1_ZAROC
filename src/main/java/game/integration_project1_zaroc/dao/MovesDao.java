package game.integration_project1_zaroc.dao;
import game.integration_project1_zaroc.model.gamelogic.Move;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * DAO responsible for saving moves to the MOVES table.
 */
public class MovesDao {

    /**
     * Inserts a single move linked with the given turn.
     *
     * @param turnId the ID of the turn this move belongs to
     * @param move   the move to save
     * @throws ZarocDaoException if the insert fails due to a database error
     */
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
