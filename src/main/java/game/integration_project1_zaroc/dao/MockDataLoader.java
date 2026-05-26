package game.integration_project1_zaroc.dao;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.InputStream;

import java.sql.*;
/**
 * Responsible for loading mockData if leaderboard is opened with no games in the database present.
 * */
public class MockDataLoader {

    /**
     * Loads mockData if no games present in database.
     * */
    public void loadIfEmpty() throws SQLException, ZarocDaoException {
        try (Connection conn = DaoUtils.createConnection()) {
            if (!isDatabaseEmpty(conn)) {
                System.out.println("[MockDataLoader] Data already present — skipping.");
                return;
            }

            System.out.println("[MockDataLoader] Database empty — loading mockData.");
            conn.setAutoCommit(false);
            try {
                loadPlayers(conn);
                loadGames(conn);
                loadGameParticipations(conn);
                loadTurns(conn);
                loadMoves(conn);
                conn.commit();
                System.out.println("[MockDataLoader] All mockData loaded successfully.");
            } catch (Exception e) {
                conn.rollback();
                System.err.println("[MockDataLoader] Rolled back: " + e.getMessage());
                throw new SQLException("Er ging iets mis bij het laden van de mockData: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (ZarocDaoException e) {
            throw new ZarocDaoException("Could not create connection to DB");
        }
    }

    /**
     * Responsible for loading the Players data
     * Copies the CSV file players.csv into the players tabel of the database.
     * */
    private void loadPlayers(Connection conn) throws Exception {
        copyInto(conn,
                "PLAYERS(username, email, play_style, difficulty, password, profile_picture)",
                "mockData/players.csv");

    }



    /**
     *  Responsible for loading the Games data
     * Creates a temporary tabel to obtain game_id's (which cannot be hardcoded) and copies the temporary tabel into the actual tabel.
     * */
    private void loadGames(Connection conn) throws Exception {
        exec(conn, """
            CREATE TEMP TABLE tmp_games (
                game_seq    INT,
                game_status TEXT
            ) ON COMMIT DROP
            """);

        copyInto(conn,
                "tmp_games(game_seq, game_status)",
                "mockData/games.csv");

        exec(conn, """
            INSERT INTO GAMES (game_status)
            SELECT game_status
            FROM   tmp_games
            ORDER  BY game_seq
            """);

    }

    /**
     * Responsible for loading the gameParticipation data
     * Creates a temporary tabel to obtain player_id and game_id after which the temp tabel is copied into the actual tabel.
     * */
    private void loadGameParticipations(Connection conn) throws Exception {
        exec(conn, """
            CREATE TEMP TABLE tmp_participations (
                username   TEXT,
                game_seq   INT,
                pawn_color TEXT,
                winner     BOOLEAN
            ) ON COMMIT DROP
            """);

        copyInto(conn,
                "tmp_participations(username, game_seq, pawn_color, winner)",
                "mockData/gameParticipation.csv");

        exec(conn, """
            INSERT INTO GAME_PARTICIPATION (player_id, game_id, pawn_color, winner)
            SELECT  p.player_id,
                    g.game_id,
                    t.pawn_color,
                    t.winner
            FROM    tmp_participations t
            JOIN    PLAYERS p ON p.username = t.username
            JOIN   (SELECT game_id,
                           ROW_NUMBER() OVER (ORDER BY game_id) - 1 AS seq
                    FROM   GAMES) g ON g.seq = t.game_seq
            """);

    }

    /**
     * Responsible for loading the Turns data
     * Creates a temporary tabel to obtain player_id and game_id after which the temp tabel is copied into the actual tabel.
     * */
    private void loadTurns(Connection conn) throws Exception {
        exec(conn, """
            CREATE TEMP TABLE tmp_turns (
                turn_number INT,
                username    TEXT,
                game_seq    INT
            ) ON COMMIT DROP
            """);

        copyInto(conn,
                "tmp_turns(turn_number, username, game_seq)",
                "mockData/turns.csv");

        exec(conn, """
            INSERT INTO TURNS (turn_number, player_id, game_id)
            SELECT  t.turn_number,
                    p.player_id,
                    g.game_id
            FROM    tmp_turns t
            JOIN    PLAYERS p ON p.username = t.username
            JOIN   (SELECT game_id,
                           ROW_NUMBER() OVER (ORDER BY game_id) - 1 AS seq
                    FROM   GAMES) g ON g.seq = t.game_seq
            """);

    }

    /**
     * Responsible for loading the Moves data
     * Creates a temporary tabel to obtain turn_id, player_id and game_id after which the temp tabel is copied into the actual tabel.
     * */
    private void loadMoves(Connection conn) throws Exception {
        exec(conn, """
            CREATE TEMP TABLE tmp_moves (
                move_number    INT,
                turn_number    INT,
                username       TEXT,
                game_seq       INT,
                start_time     TEXT,
                end_time       TEXT,
                start_location TEXT,
                end_location   TEXT
            ) ON COMMIT DROP
            """);

        copyInto(conn,
                "tmp_moves(move_number, turn_number, username, game_seq, " +
                        "start_time, end_time, start_location, end_location)",
                "mockData/moves.csv");

        exec(conn, """
            INSERT INTO MOVES (move_number, turn_id,
                               start_time, end_time,
                               start_location, end_location)
            SELECT  m.move_number,
                    t.turn_id,
                    m.start_time::TIMESTAMP,
                    m.end_time::TIMESTAMP,
                    m.start_location,
                    m.end_location
            FROM    tmp_moves m
            JOIN    PLAYERS p ON p.username    = m.username
            JOIN   (SELECT game_id,
                           ROW_NUMBER() OVER (ORDER BY game_id) - 1 AS seq
                    FROM   GAMES) g ON g.seq   = m.game_seq
            JOIN    TURNS t ON  t.turn_number  = m.turn_number
                            AND t.player_id    = p.player_id
                            AND t.game_id      = g.game_id
            """);

    }

    /**
     * Helper method for copying csv files into database.
     * */
    private void copyInto(Connection conn, String tableAndCols,
                          String resourcePath) throws Exception {
        try (InputStream is = getClass()
                .getResourceAsStream("/" + resourcePath)){
            System.out.println("Loading resource: " + resourcePath);
            if (is == null)
                throw new RuntimeException("CSV file not found: " + resourcePath);
            new CopyManager(conn.unwrap(BaseConnection.class))
                    .copyIn("COPY " + tableAndCols +
                            " FROM STDIN DELIMITER ',' CSV HEADER", is);
        }
    }

    /**
     * Helper method for actually executing the sql statement.
     * */
    private void exec(Connection conn, String sql) throws SQLException {
        conn.createStatement().execute(sql);
    }

    /**
     * Method used to see if database has games stored.
     * Called before loading mockData
     * */
    private boolean isDatabaseEmpty(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM GAMES");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() && rs.getInt(1) == 0;
        }
    }

    /**
     * Method for removing all data from database.
     * Testing only.
     * */
    public void clearDatabase() throws SQLException, ZarocDaoException {
        try (Connection conn = DaoUtils.createConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(
                    "TRUNCATE TABLE GAME_ROOMS, MOVES, TURNS, GAME_PARTICIPATION, GAMES, PLAYERS RESTART IDENTITY CASCADE"
            );

            System.out.println("[MockDataLoader] Database cleared completely.");
        }
    }
}