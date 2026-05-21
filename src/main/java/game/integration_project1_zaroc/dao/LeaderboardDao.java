package game.integration_project1_zaroc.dao;



import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsible for fetching player statistics for the leaderboard.
 */
public class LeaderboardDao {

    /**
     * Fetches all players who have participated in at least one finished game,
     * along with their statistics (only ENDED games count towards these). Results are ordered by win percentage
     * descending, then wins descending, then username ascending.
     *
     * @return a ranked list of {@link LeaderboardEntry} objects, one per player;
     *         empty if no finished games are found
     * @throws SQLException      if a database access error occurs
     * @throws ZarocDaoException if the database connection cannot be established
     */
    public List<LeaderboardEntry> fetchLeaderboard() throws SQLException, ZarocDaoException {

        String sql = """
                SELECT
                p.username,
                COUNT(DISTINCT gp.game_id)                                  AS games_played,

                COUNT(DISTINCT CASE WHEN gp.winner = TRUE THEN gp.game_id END)\s
                                                                            AS wins,
                COUNT(DISTINCT gp.game_id) - COUNT(DISTINCT CASE WHEN gp.winner = TRUE THEN gp.game_id END)
                                                                                                      AS losses,
                ROUND(
                    (100.0 * COUNT(DISTINCT CASE WHEN gp.winner = TRUE THEN gp.game_id END)
                    / NULLIF(COUNT(DISTINCT gp.game_id), 0))::NUMERIC, 1
                    )                                                           AS win_percentage,
                COALESCE(
                    SUM(EXTRACT(EPOCH FROM (m.end_time - m.start_time))::BIGINT), 0
                    )                                                           AS total_play_time_sec,
                COALESCE(
                    ROUND(COUNT(m.move_id)::NUMERIC
                    / NULLIF(COUNT(DISTINCT gp.game_id), 0), 2), 0
                    )                                                           AS avg_moves_per_game,
                COALESCE(
                    ROUND(
                         (SUM(EXTRACT(EPOCH FROM (m.end_time - m.start_time)))
                         / NULLIF(COUNT(m.move_id), 0))::NUMERIC, 2
                         ), 0
                         )                                                           AS avg_sec_per_move,
                COUNT(DISTINCT CASE WHEN gp.winner = TRUE THEN gp.game_id END)
                                                                                                      AS total_score
                                      FROM
                                          PLAYERS p
                                          JOIN GAME_PARTICIPATION gp ON p.player_id = gp.player_id
                                          JOIN GAMES g              ON gp.game_id  = g.game_id
                                          LEFT JOIN TURNS t         ON t.player_id = p.player_id
                                                                   AND t.game_id  = gp.game_id
                                          LEFT JOIN MOVES m         ON m.turn_id   = t.turn_id
                                      WHERE
                                          g.game_status = 'ENDED'
                                      GROUP BY
                                          p.player_id, p.username
                                      ORDER BY
                                          win_percentage DESC,
                                          wins           DESC,
                                          p.username     ASC;
                """;

        List<LeaderboardEntry> entries = new ArrayList<>();

        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int rank = 1;
            while (rs.next()) {
                LeaderboardEntry entry = new LeaderboardEntry(
                    rank++,
                    rs.getString("username"),
                    rs.getInt("games_played"),
                    rs.getInt("wins"),
                    rs.getInt("losses"),
                    rs.getDouble("win_percentage"),
                    rs.getLong("total_play_time_sec"),
                    rs.getDouble("avg_moves_per_game"),
                    rs.getDouble("avg_sec_per_move"),
                    rs.getInt("total_score")
                );
                entries.add(entry);
            }
        } catch (ZarocDaoException e) {
            throw new ZarocDaoException("Kon niets ophalen.", e);
        }

        System.out.println("[DB] Leaderboard fetched: " + entries.size() + " players.");
        return entries;
    }


    }

