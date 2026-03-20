package game.integration_project1_zaroc.dao;
import game.integration_project1_zaroc.model.gamelogic.Move;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;


public class MovesDao {


    public void createMove(Move move) throws ZarocDaoException {
        try (Connection conn = DaoUtils.createConnection()) {

            //get the starttime from the db by asking for the endtime of the previous move.

            Timestamp startTime;
            String lastMoveSql = "SELECT end_time FROM MOVES WHERE turn_id = ? ORDER BY move_number DESC FETCH FIRST 1 ROWS ONLY";
            try (PreparedStatement psLast = conn.prepareStatement(lastMoveSql)) {
                psLast.setInt(1, move.getTurn().getTurnId());
                try (var rs = psLast.executeQuery()) {
                    if (rs.next()) startTime = rs.getTimestamp("end_time");
                    else startTime = new Timestamp(System.currentTimeMillis());
                }
            }

            Timestamp endTime = new Timestamp(System.currentTimeMillis());



            int moveNumber = 1;
            String moveNumSql = "SELECT COUNT(*) + 1 FROM MOVES WHERE turn_id = ?";
            try (PreparedStatement psCount = conn.prepareStatement(moveNumSql)) {
                psCount.setInt(1, move.getTurn().getTurnId());
                try (var rs = psCount.executeQuery()) {
                    if (rs.next()) {
                        moveNumber = rs.getInt(1);
                        if (moveNumber > 2) {
                            throw new ZarocDaoException("Er zitten al twee moves in deze turn, kan er geen meer toevoegen");
                        }
                    }
                }
            }


            String sql = "INSERT INTO MOVES (move_number, turn_id, start_time, end_time, start_location, end_location) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, moveNumber);
                ps.setInt(2, move.getTurn().getTurnId());
                ps.setTimestamp(3, startTime);
                ps.setTimestamp(4, endTime);
                ps.setString(5, move.getStartPeg().toString());
                ps.setString(6, move.getDestinationPeg().toString());

                ps.executeUpdate();
            }

        } catch (SQLException e) {
            throw new ZarocDaoException("Kon geen move aanmaken", e);
        }
    }
}
