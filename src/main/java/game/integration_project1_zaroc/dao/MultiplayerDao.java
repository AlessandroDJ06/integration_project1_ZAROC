package game.integration_project1_zaroc.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MultiplayerDao {
    public List<MultiplayerMove> fetchNewMoves(int gameId, int offset, String myUsername) throws ZarocDaoException {
        List<MultiplayerMove> newMoves = new ArrayList<>();

        String sql = """
                SELECT m.turn_id,
                       m.move_number,
                       m.start_time,
                       m.end_time,
                       m.start_location,
                       m.end_location,
                       p.username
                FROM moves m
                         JOIN turns t ON m.turn_id = t.turn_id
                         JOIN players p ON t.player_id = p.player_id
                WHERE t.game_id = ?
                  AND p.username != ?
                ORDER BY t.turn_number ASC, m.move_number ASC
                LIMIT 100 OFFSET ?;
                """;

        try (java.sql.Connection conn = DaoUtils.createConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gameId);
            ps.setString(2, myUsername);
            ps.setInt(3, offset);

            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    newMoves.add(new MultiplayerMove(
                            rs.getInt("turn_id"),
                            rs.getInt("move_number"),
                            rs.getTimestamp("start_time"),
                            rs.getTimestamp("end_time"),
                            rs.getString("start_location"),
                            rs.getString("end_location"),
                            rs.getString("username")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new ZarocDaoException("Fout bij het ophalen van nieuwe multiplayer zetten", e);
        }

        return newMoves;
    }
}