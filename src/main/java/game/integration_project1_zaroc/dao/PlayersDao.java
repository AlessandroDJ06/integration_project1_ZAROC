package game.integration_project1_zaroc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlayersDao {

    public int createPlayer(String username, String email, String playStyle, String difficulty, String password) throws ZarocDaoException {

        String sql = "INSERT INTO PLAYERS (username, email, play_style, difficulty, password) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, playStyle);       //voeg hier voor deze twee miss een onbepaald toe voor nu in de enum
            ps.setString(4, difficulty);
            ps.setString(5, password);

            ps.executeUpdate();


            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new ZarocDaoException("Player werd aan de DB toegevoegd maar er werd geen ID teruggegeven");
            }

        } catch (SQLException e) {
            throw new ZarocDaoException("Kon geen nieuwe speler maken", e);
        }
    }





}
