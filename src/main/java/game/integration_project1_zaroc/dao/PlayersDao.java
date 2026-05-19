package game.integration_project1_zaroc.dao;

import game.integration_project1_zaroc.model.players.AIPlayer;
import game.integration_project1_zaroc.model.players.HumanPlayer;
import game.integration_project1_zaroc.model.players.Player;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO responsible for creating, retrieving, and updating player records in the PLAYERS table.
 */
public class PlayersDao {

    /**
     * Inserts a new human player record with a BCrypt-hashed password.
     *
     * @param player   the human player to persist
     * @param password the plain-text password to hash and store
     * @return the generated {@code player_id} of the newly created player
     * @throws ZarocDaoException if the insert fails or no ID is returned
     */
    public int createHumanPlayer(HumanPlayer player, String password) throws ZarocDaoException {
        String sql = "INSERT INTO PLAYERS (username, email, password, play_style,profile_picture) VALUES (?, ?, ?, NULL,?)";

        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, player.getUsername());
            ps.setString(2, player.getEmail());
            ps.setString(3, BCrypt.hashpw(password, BCrypt.gensalt()));
            ps.setString(4, player.getProfilePicture());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new ZarocDaoException("Geen ID teruggegeven");
            }

        } catch (SQLException e) {
            throw new ZarocDaoException("Kon geen nieuwe speler maken", e);
        }
    }
    /**
     * Inserts a new AI player record with the player's difficulty and profile picture.
     *
     * @param player the AI player to save
     * @return the generated {@code player_id} of the newly created AI player
     * @throws ZarocDaoException if the insert fails or no ID is returned
     */
    public int createAiPlayer(AIPlayer player) throws ZarocDaoException {
        String sql = "INSERT INTO PLAYERS (username, email, play_style,difficulty,profile_picture) VALUES (?, NULL, NULL,?,?)";

        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, player.getUsername());
            //ps.setString(3, player.getPlayerStyle().toString());
            ps.setString(2,player.getDifficulty().toString());
            ps.setString(3, player.getProfilePicture());

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

    /**
     * Retrieves a human player by username and validates the provided password.
     *
     * @param username the username to search for
     * @param password the plain-text password to check with the stored hash
     * @return the matching {@link HumanPlayer}, or {@code null} if no player is found
     * @throws ZarocDaoException if the password is incorrect or the database is unreachable
     */
    public HumanPlayer getPlayerByUsername(String username, String password) throws ZarocDaoException {
        String sql = "SELECT * FROM PLAYERS WHERE username = ?";

        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hash = rs.getString("password");
                    if (!BCrypt.checkpw(password, hash)) {
                        throw new ZarocDaoException("Ongeldig wachtwoord");
                    }
                    return HumanPlayer.fromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            throw new ZarocDaoException("Database is onbereikbaar", e);
        }

        return null;
    }

    /**
     * Returns the {@code player_id} of an existing AI player matching the given username
     * and difficulty, or creates a new one if no match is found.
     *
     * @param player the AI player to look up or create
     * @return the existing or newly created {@code player_id}
     * @throws ZarocDaoException if the lookup or creation fails
     */
    public int getOrCreateAiPlayer(AIPlayer player) throws ZarocDaoException {
        String selectSql = "SELECT player_id FROM PLAYERS WHERE username = ? AND difficulty = ?";

        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(selectSql)) {

            ps.setString(1, player.getUsername());
            ps.setString(2, player.getDifficulty().toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("player_id");
                }
            }

        } catch (SQLException e) {
            throw new ZarocDaoException("Kon AI speler niet ophalen", e);
        }
        return createAiPlayer(player);
    }

    /**
     * Retrieves a human player by their ID.
     *
     * @param playerId the ID of the player to retrieve
     * @return the matching {@link HumanPlayer}, or {@code null} if not found
     * @throws ZarocDaoException if the database is unreachable
     */
    public HumanPlayer getPlayerById(int playerId) throws ZarocDaoException {
        String sql = "SELECT * FROM PLAYERS WHERE player_id = ?";

        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, playerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return HumanPlayer.fromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            throw new ZarocDaoException("Database is onbereikbaar", e);
        }

        return null;
    }

    /**
     * Updates the play style of an existing player.
     *
     * @param player the player whose play style should be changed, must have a valid {@code playerId}
     * @throws ZarocDaoException if the update fails due to a database error
     */
    public void updatePlayerPlaystyle(Player player) throws ZarocDaoException{
        String sql = "UPDATE PLAYERS SET playstyle = ? WHERE player_id = ?";

        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,player.getPlayerStyle().toString());
            ps.setInt(2,player.getPlayerId());

        } catch (SQLException sqlException){
            throw new ZarocDaoException("Kon de speler niet updaten.", sqlException);
    }

}


    /**
     * Returns the total number of games played by the given player across all statuses.
     *
     * @param playerId the ID of the player to look for
     * @return total games played, or {@code 0} if none are found
     * @throws ZarocDaoException if the query fails
     */
    public int getTotalGamesPlayed(int playerId) throws ZarocDaoException {
        String sql = "SELECT COUNT(*) FROM games g JOIN game_participation gp ON g.game_id = gp.game_id WHERE gp.player_id = ?";

        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, playerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new ZarocDaoException("Kon aantal games niet ophalen", e);
        }
        return 0;
    }

    /**
     * Returns the total number of wins for the given player.
     *
     * @param playerId the ID of the player to look for
     * @return total wins, or {@code 0} if none are found
     * @throws ZarocDaoException if the query fails
     */
    public int getTotalWins(int playerId) throws ZarocDaoException {
        String sql = "SELECT COUNT(*) FROM game_participation WHERE player_id = ? AND winner = true";
        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, playerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new ZarocDaoException("Kon aantal wins niet ophalen", e);
        }
        return 0;
    }
    /**
     * Returns the difficulty level most frequently faced by the given player
     * across all their games.
     *
     * @param playerId the ID of the player to look for
     * @return the most common difficulty string, or {@code "X"} if none is found
     * @throws ZarocDaoException if the query fails
     */
    public String getMostUsedDifficulty(int playerId) throws ZarocDaoException {
        String sql = """
        SELECT p.difficulty FROM players p
        JOIN game_participation gp ON p.player_id = gp.player_id
        JOIN game_participation gp2 ON gp.game_id = gp2.game_id
        WHERE gp2.player_id = ? AND p.difficulty IS NOT NULL
        GROUP BY p.difficulty ORDER BY COUNT(*) DESC LIMIT 1
        """;
        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, playerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("difficulty");
            }
        } catch (SQLException e) {
            throw new ZarocDaoException("Kon moeilijkheidsgraad niet ophalen", e);
        }
        return "X";
    }
}
