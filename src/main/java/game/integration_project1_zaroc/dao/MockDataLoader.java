package game.integration_project1_zaroc.dao;

import java.sql.*;

/**
  * TRIGGERED: Call loadIfEmpty() once from Application.start() before any
 *            scene is shown.
 */
public class MockDataLoader {
//
//    private final Connection conn;
//
//    public MockDataLoader() throws SQLException, ZarocDaoException {
//        this.conn = DaoUtils.createConnection();
//    }

    // ══════════════════════════════════════════════════════════════════════
    //  Public entry point
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Loads mock data only when the GAMES table is empty.
     * Safe to call every startup — it does nothing if data already exists.
     */
    public void loadIfEmpty() throws SQLException, ZarocDaoException {
        try(Connection conn = DaoUtils.createConnection()){
        if (isDatabaseEmpty(conn)) {
            System.out.println("[MockDataLoader] Database empty — loading mock data.");
            conn.setAutoCommit(false);
            try {
                insertPlayers(conn);
                insertGames(conn);
                insertParticipations(conn);
                insertTurns(conn);
                insertMoves(conn);
                conn.commit();
                System.out.println("[MockDataLoader] Mock data loaded successfully.");
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("[MockDataLoader] Rolled back: " + e.getMessage());
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } else {
            System.out.println("[MockDataLoader] Data already present — skipping.");
        }
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Empty check
    // ══════════════════════════════════════════════════════════════════════

    private boolean isDatabaseEmpty(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM GAMES");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() && rs.getInt(1) == 0;
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Players
    // ══════════════════════════════════════════════════════════════════════

    private void insertPlayers(Connection conn) throws SQLException {
        String sql = """
            INSERT INTO PLAYERS (username, email, play_style, password)
            VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING
            """;
        Object[][] players = {
                {"Alice",   "alice@game.com",    "AGGRESSIVE", "hashed_alice"},
                {"Bob",     "bob@game.com",        "PASSIVE",    "hashed_bob"},
                {"Charlie", "charlie@game.com",    "AGGRESSIVE", "hashed_charlie"},
                {"Diana",   "diana@game.com",    "PASSIVE",    "hashed_diana"},
                {"Eve",     "eve@game.com",    "AGGRESSIVE", "hashed_eve"},
        };
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] p : players) {
                ps.setString(1, (String) p[0]);
                ps.setString(2, (String) p[1]);
                ps.setString(3, (String) p[2]);
                ps.setString(4, (String) p[3]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Games
    // ══════════════════════════════════════════════════════════════════════

    private void insertGames(Connection conn) throws SQLException {
        String sql = "INSERT INTO GAMES (game_status) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < 13; i++) { ps.setString(1, "ENDED");    ps.addBatch(); }
            for (int i = 0; i < 2;  i++) { ps.setString(1, "PAUSED"); ps.addBatch(); }
            ps.executeBatch();
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Game Participation — no hardcoded IDs, subselects on username
    // ══════════════════════════════════════════════════════════════════════

    private void insertParticipations(Connection conn) throws SQLException {
        // { playerUsername, gameOffset(0-based), pawnColor, winnerUsername or null }
        Object[][] rows = {
                {"Alice",   0, "RED",    "Alice"},   // Game 1: Alice beats Bob
                {"Bob",     0, "BLUE",   "Alice"},
                {"Alice",   1, "RED",    "Bob"},     // Game 2: Bob beats Alice
                {"Bob",     1, "BLUE",   "Bob"},
                {"Alice",   2, "RED",    "Alice"},   // Game 3: Alice beats Bob
                {"Bob",     2, "BLUE",   "Alice"},
                {"Alice",   3, "RED",    "Charlie"}, // Game 4: Charlie beats Alice
                {"Charlie", 3, "GREEN",  "Charlie"},
                {"Alice",   4, "RED",    "Alice"},   // Game 5: Alice beats Charlie
                {"Charlie", 4, "GREEN",  "Alice"},
                {"Bob",     5, "BLUE",   "Diana"},   // Game 6: Diana beats Bob
                {"Diana",   5, "YELLOW", "Diana"},
                {"Bob",     6, "BLUE",   "Bob"},     // Game 7: Bob beats Diana
                {"Diana",   6, "YELLOW", "Bob"},
                {"Charlie", 7, "GREEN",  "Eve"},     // Game 8: Eve beats Charlie
                {"Eve",     7, "PURPLE", "Eve"},
                {"Charlie", 8, "GREEN",  "Charlie"}, // Game 9: Charlie beats Eve
                {"Eve",     8, "PURPLE", "Charlie"},
                {"Diana",   9, "YELLOW", "Eve"},     // Game 10: Eve beats Diana
                {"Eve",     9, "PURPLE", "Eve"},
                {"Diana",  10, "YELLOW", "Diana"},   // Game 11: Diana beats Eve
                {"Eve",    10, "PURPLE", "Diana"},
                {"Alice",  11, "RED",    "Alice"},   // Game 12: Alice beats Diana
                {"Diana",  11, "YELLOW", "Alice"},
                {"Bob",    12, "BLUE",   "Eve"},     // Game 13: Eve beats Bob
                {"Eve",    12, "PURPLE", "Eve"},
                {"Alice",  13, "RED",    null},      // Game 14: IN_PROGRESS
                {"Charlie",13, "GREEN",  null},
                {"Bob",    14, "BLUE",   null},      // Game 15: IN_PROGRESS
                {"Eve",    14, "PURPLE", null},
        };

        String sqlWithWinner = """
            INSERT INTO GAME_PARTICIPATION (player_id, game_id, pawn_color, winner_id)
            VALUES (
                (SELECT player_id FROM PLAYERS WHERE username = ?),
                (SELECT game_id   FROM GAMES   ORDER BY game_id LIMIT 1 OFFSET ?),
                ?,
                (SELECT player_id FROM PLAYERS WHERE username = ?)
            )""";
        String sqlNullWinner = """
            INSERT INTO GAME_PARTICIPATION (player_id, game_id, pawn_color, winner_id)
            VALUES (
                (SELECT player_id FROM PLAYERS WHERE username = ?),
                (SELECT game_id   FROM GAMES   ORDER BY game_id LIMIT 1 OFFSET ?),
                ?, NULL
            )""";

        for (Object[] row : rows) {
            String winner = (String) row[3];
            if (winner != null) {
                try (PreparedStatement ps = conn.prepareStatement(sqlWithWinner)) {
                    ps.setString(1, (String) row[0]);
                    ps.setInt(2,    (int)    row[1]);
                    ps.setString(3, (String) row[2]);
                    ps.setString(4, winner);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(sqlNullWinner)) {
                    ps.setString(1, (String) row[0]);
                    ps.setInt(2,    (int)    row[1]);
                    ps.setString(3, (String) row[2]);
                    ps.executeUpdate();
                }
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Turns
    // ══════════════════════════════════════════════════════════════════════

    private void insertTurns(Connection conn) throws SQLException {
        // { turnNumber, playerUsername, gameOffset }
        Object[][] turns = {
                {1,"Alice",0},{2,"Bob",0},{3,"Alice",0},{4,"Bob",0},
                {1,"Alice",1},{2,"Bob",1},{3,"Alice",1},{4,"Bob",1},
                {1,"Alice",2},{2,"Bob",2},{3,"Alice",2},{4,"Bob",2},
                {1,"Alice",3},{2,"Charlie",3},{3,"Alice",3},{4,"Charlie",3},
                {1,"Alice",4},{2,"Charlie",4},{3,"Alice",4},{4,"Charlie",4},
                {1,"Bob",5},{2,"Diana",5},{3,"Bob",5},{4,"Diana",5},
                {1,"Bob",6},{2,"Diana",6},{3,"Bob",6},{4,"Diana",6},
                {1,"Charlie",7},{2,"Eve",7},{3,"Charlie",7},{4,"Eve",7},
                {1,"Charlie",8},{2,"Eve",8},{3,"Charlie",8},{4,"Eve",8},
                {1,"Diana",9},{2,"Eve",9},{3,"Diana",9},{4,"Eve",9},
                {1,"Diana",10},{2,"Eve",10},{3,"Diana",10},{4,"Eve",10},
                {1,"Alice",11},{2,"Diana",11},{3,"Alice",11},{4,"Diana",11},
                {1,"Bob",12},{2,"Eve",12},{3,"Bob",12},{4,"Eve",12},
                // in-progress
                {1,"Alice",13},{2,"Charlie",13},
                {1,"Bob",14},{2,"Eve",14},
        };
        String sql = """
            INSERT INTO TURNS (turn_number, player_id, game_id)
            VALUES (?,
                (SELECT player_id FROM PLAYERS WHERE username = ?),
                (SELECT game_id   FROM GAMES   ORDER BY game_id LIMIT 1 OFFSET ?))
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] t : turns) {
                ps.setInt(1,    (int)    t[0]);
                ps.setString(2, (String) t[1]);
                ps.setInt(3,    (int)    t[2]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }



    private void insertMoves(Connection conn) throws SQLException {
        // { moveNumber, turnNumber, playerUsername, gameOffset,
        //   startTime, endTime, startLoc, endLoc }
        Object[][] moves = {
                // ── Game 1 ──
                {1,1,"Alice",0, "2024-01-10 10:00:00","2024-01-10 10:00:14","1,0","3,0"},
                {2,1,"Alice",0, "2024-01-10 10:00:14","2024-01-10 10:00:30","3,0","2,2"},
                {1,2,"Bob",  0, "2024-01-10 10:00:35","2024-01-10 10:00:52","7,0","5,0"},
                {2,2,"Bob",  0, "2024-01-10 10:00:52","2024-01-10 10:01:10","5,0","6,2"},
                {1,3,"Alice",0, "2024-01-10 10:01:15","2024-01-10 10:01:28","2,2","2,3"},
                {2,3,"Alice",0, "2024-01-10 10:01:28","2024-01-10 10:01:44","1,1","3,1"},
                {1,4,"Bob",  0, "2024-01-10 10:01:50","2024-01-10 10:02:06","6,2","4,2"},
                {2,4,"Bob",  0, "2024-01-10 10:02:06","2024-01-10 10:02:22","6,2","6,3"},
                // ── Game 2 ──
                {1,1,"Alice",1, "2024-01-11 09:00:00","2024-01-11 09:00:16","1,0","3,0"},
                {2,1,"Alice",1, "2024-01-11 09:00:16","2024-01-11 09:00:32","5,0","4,2"},
                {1,2,"Bob",  1, "2024-01-11 09:00:36","2024-01-11 09:00:52","7,0","5,0"},
                {2,2,"Bob",  1, "2024-01-11 09:00:52","2024-01-11 09:01:10","5,0","6,2"},
                {1,3,"Alice",1, "2024-01-11 09:01:14","2024-01-11 09:01:28","4,2","4,3"},
                {2,3,"Alice",1, "2024-01-11 09:01:28","2024-01-11 09:01:45","3,0","2,2"},
                {1,4,"Bob",  1, "2024-01-11 09:01:50","2024-01-11 09:02:06","6,2","6,3"},
                {2,4,"Bob",  1, "2024-01-11 09:02:06","2024-01-11 09:02:22","5,1","4,2"},
                // ── Game 3 ──
                {1,1,"Alice",2, "2024-01-12 14:00:00","2024-01-12 14:00:12","3,0","1,0"},
                {2,1,"Alice",2, "2024-01-12 14:00:12","2024-01-12 14:00:28","1,0","0,2"},
                {1,2,"Bob",  2, "2024-01-12 14:00:32","2024-01-12 14:00:47","5,1","7,1"},
                {2,2,"Bob",  2, "2024-01-12 14:00:47","2024-01-12 14:01:04","7,1","8,2"},
                {1,3,"Alice",2, "2024-01-12 14:01:08","2024-01-12 14:01:22","0,2","0,3"},
                {2,3,"Alice",2, "2024-01-12 14:01:22","2024-01-12 14:01:38","5,0","4,2"},
                {1,4,"Bob",  2, "2024-01-12 14:01:42","2024-01-12 14:01:58","8,2","6,2"},
                {2,4,"Bob",  2, "2024-01-12 14:01:58","2024-01-12 14:02:14","6,2","6,3"},
                // ── Game 4 ──
                {1,1,"Alice",  3,"2024-01-13 10:00:00","2024-01-13 10:00:15","1,0","3,0"},
                {2,1,"Alice",  3,"2024-01-13 10:00:15","2024-01-13 10:00:32","3,0","2,2"},
                {1,2,"Charlie",3,"2024-01-13 10:00:36","2024-01-13 10:00:52","5,0","7,0"},
                {2,2,"Charlie",3,"2024-01-13 10:00:52","2024-01-13 10:01:08","7,0","8,2"},
                {1,3,"Alice",  3,"2024-01-13 10:01:12","2024-01-13 10:01:26","2,2","2,3"},
                {2,3,"Alice",  3,"2024-01-13 10:01:26","2024-01-13 10:01:42","1,1","0,2"},
                {1,4,"Charlie",3,"2024-01-13 10:01:46","2024-01-13 10:02:02","8,2","8,3"},
                {2,4,"Charlie",3,"2024-01-13 10:02:02","2024-01-13 10:02:18","3,1","4,2"},
                // ── Game 5 ──
                {1,1,"Alice",  4,"2024-01-14 11:00:00","2024-01-14 11:00:14","7,0","5,0"},
                {2,1,"Alice",  4,"2024-01-14 11:00:14","2024-01-14 11:00:30","5,0","4,2"},
                {1,2,"Charlie",4,"2024-01-14 11:00:34","2024-01-14 11:00:50","1,0","3,0"},
                {2,2,"Charlie",4,"2024-01-14 11:00:50","2024-01-14 11:01:07","3,0","2,2"},
                {1,3,"Alice",  4,"2024-01-14 11:01:11","2024-01-14 11:01:25","4,2","4,3"},
                {2,3,"Alice",  4,"2024-01-14 11:01:25","2024-01-14 11:01:40","3,1","2,2"},
                {1,4,"Charlie",4,"2024-01-14 11:01:44","2024-01-14 11:02:00","2,2","2,3"},
                {2,4,"Charlie",4,"2024-01-14 11:02:00","2024-01-14 11:02:16","5,1","6,2"},
                // ── Game 6 ──
                {1,1,"Bob",  5,"2024-01-15 09:00:00","2024-01-15 09:00:18","1,0","3,0"},
                {2,1,"Bob",  5,"2024-01-15 09:00:18","2024-01-15 09:00:36","3,0","4,2"},
                {1,2,"Diana",5,"2024-01-15 09:00:40","2024-01-15 09:00:57","7,1","5,1"},
                {2,2,"Diana",5,"2024-01-15 09:00:57","2024-01-15 09:01:14","5,1","6,2"},
                {1,3,"Bob",  5,"2024-01-15 09:01:18","2024-01-15 09:01:33","4,2","2,2"},
                {2,3,"Bob",  5,"2024-01-15 09:01:33","2024-01-15 09:01:49","2,2","2,3"},
                {1,4,"Diana",5,"2024-01-15 09:01:53","2024-01-15 09:02:09","6,2","6,3"},
                {2,4,"Diana",5,"2024-01-15 09:02:09","2024-01-15 09:02:25","3,0","4,2"},
                // ── Game 7 ──
                {1,1,"Bob",  6,"2024-01-16 10:00:00","2024-01-16 10:00:14","5,0","7,0"},
                {2,1,"Bob",  6,"2024-01-16 10:00:14","2024-01-16 10:00:30","7,0","8,2"},
                {1,2,"Diana",6,"2024-01-16 10:00:34","2024-01-16 10:00:50","1,1","3,1"},
                {2,2,"Diana",6,"2024-01-16 10:00:50","2024-01-16 10:01:06","3,1","2,2"},
                {1,3,"Bob",  6,"2024-01-16 10:01:10","2024-01-16 10:01:24","8,2","8,3"},
                {2,3,"Bob",  6,"2024-01-16 10:01:24","2024-01-16 10:01:40","3,0","4,2"},
                {1,4,"Diana",6,"2024-01-16 10:01:44","2024-01-16 10:02:00","2,2","2,3"},
                {2,4,"Diana",6,"2024-01-16 10:02:00","2024-01-16 10:02:16","1,0","0,2"},
                // ── Game 8 ──
                {1,1,"Charlie",7,"2024-01-17 13:00:00","2024-01-17 13:00:20","3,0","1,0"},
                {2,1,"Charlie",7,"2024-01-17 13:00:20","2024-01-17 13:00:38","1,0","0,2"},
                {1,2,"Eve",    7,"2024-01-17 13:00:42","2024-01-17 13:00:58","7,0","5,0"},
                {2,2,"Eve",    7,"2024-01-17 13:00:58","2024-01-17 13:01:15","5,0","6,2"},
                {1,3,"Charlie",7,"2024-01-17 13:01:19","2024-01-17 13:01:34","0,2","0,3"},
                {2,3,"Charlie",7,"2024-01-17 13:01:34","2024-01-17 13:01:50","5,1","4,2"},
                {1,4,"Eve",    7,"2024-01-17 13:01:54","2024-01-17 13:02:10","6,2","6,3"},
                {2,4,"Eve",    7,"2024-01-17 13:02:10","2024-01-17 13:02:26","3,1","4,2"},
                // ── Game 9 ──
                {1,1,"Charlie",8,"2024-01-18 09:00:00","2024-01-18 09:00:14","1,0","3,0"},
                {2,1,"Charlie",8,"2024-01-18 09:00:14","2024-01-18 09:00:30","3,0","4,2"},
                {1,2,"Eve",    8,"2024-01-18 09:00:34","2024-01-18 09:00:50","7,1","5,1"},
                {2,2,"Eve",    8,"2024-01-18 09:00:50","2024-01-18 09:01:06","5,1","6,2"},
                {1,3,"Charlie",8,"2024-01-18 09:01:10","2024-01-18 09:01:24","4,2","4,3"},
                {2,3,"Charlie",8,"2024-01-18 09:01:24","2024-01-18 09:01:40","5,0","6,2"},
                {1,4,"Eve",    8,"2024-01-18 09:01:44","2024-01-18 09:02:00","6,2","6,3"},
                {2,4,"Eve",    8,"2024-01-18 09:02:00","2024-01-18 09:02:16","1,1","2,2"},
                // ── Game 10 ──
                {1,1,"Diana",9,"2024-01-19 15:00:00","2024-01-19 15:00:16","3,1","1,1"},
                {2,1,"Diana",9,"2024-01-19 15:00:16","2024-01-19 15:00:33","1,1","0,2"},
                {1,2,"Eve",  9,"2024-01-19 15:00:37","2024-01-19 15:00:52","5,0","7,0"},
                {2,2,"Eve",  9,"2024-01-19 15:00:52","2024-01-19 15:01:09","7,0","8,2"},
                {1,3,"Diana",9,"2024-01-19 15:01:13","2024-01-19 15:01:28","0,2","0,3"},
                {2,3,"Diana",9,"2024-01-19 15:01:28","2024-01-19 15:01:44","1,0","2,2"},
                {1,4,"Eve",  9,"2024-01-19 15:01:48","2024-01-19 15:02:04","8,2","8,3"},
                {2,4,"Eve",  9,"2024-01-19 15:02:04","2024-01-19 15:02:20","3,0","2,2"},
                // ── Game 11 ──
                {1,1,"Diana",10,"2024-01-20 10:00:00","2024-01-20 10:00:14","7,1","5,1"},
                {2,1,"Diana",10,"2024-01-20 10:00:14","2024-01-20 10:00:30","5,1","4,2"},
                {1,2,"Eve",  10,"2024-01-20 10:00:34","2024-01-20 10:00:50","1,0","3,0"},
                {2,2,"Eve",  10,"2024-01-20 10:00:50","2024-01-20 10:01:06","3,0","2,2"},
                {1,3,"Diana",10,"2024-01-20 10:01:10","2024-01-20 10:01:24","4,2","4,3"},
                {2,3,"Diana",10,"2024-01-20 10:01:24","2024-01-20 10:01:40","3,1","2,2"},
                {1,4,"Eve",  10,"2024-01-20 10:01:44","2024-01-20 10:02:00","2,2","2,3"},
                {2,4,"Eve",  10,"2024-01-20 10:02:00","2024-01-20 10:02:16","5,0","6,2"},
                // ── Game 12 ──
                {1,1,"Alice",11,"2024-01-21 11:00:00","2024-01-21 11:00:13","1,0","3,0"},
                {2,1,"Alice",11,"2024-01-21 11:00:13","2024-01-21 11:00:28","3,0","2,2"},
                {1,2,"Diana",11,"2024-01-21 11:00:32","2024-01-21 11:00:48","5,1","7,1"},
                {2,2,"Diana",11,"2024-01-21 11:00:48","2024-01-21 11:01:05","7,1","8,2"},
                {1,3,"Alice",11,"2024-01-21 11:01:09","2024-01-21 11:01:22","2,2","2,3"},
                {2,3,"Alice",11,"2024-01-21 11:01:22","2024-01-21 11:01:38","5,0","4,2"},
                {1,4,"Diana",11,"2024-01-21 11:01:42","2024-01-21 11:01:57","8,2","6,2"},
                {2,4,"Diana",11,"2024-01-21 11:01:57","2024-01-21 11:02:13","6,2","6,3"},
                // ── Game 13 ──
                {1,1,"Bob",12,"2024-01-22 14:00:00","2024-01-22 14:00:15","3,0","1,0"},
                {2,1,"Bob",12,"2024-01-22 14:00:15","2024-01-22 14:00:32","1,0","0,2"},
                {1,2,"Eve", 12,"2024-01-22 14:00:36","2024-01-22 14:00:52","7,0","5,0"},
                {2,2,"Eve", 12,"2024-01-22 14:00:52","2024-01-22 14:01:09","5,0","4,2"},
                {1,3,"Bob",12,"2024-01-22 14:01:13","2024-01-22 14:01:28","0,2","0,3"},
                {2,3,"Bob",12,"2024-01-22 14:01:28","2024-01-22 14:01:44","5,1","4,2"},
                {1,4,"Eve", 12,"2024-01-22 14:01:48","2024-01-22 14:02:04","4,2","4,3"},
                {2,4,"Eve", 12,"2024-01-22 14:02:04","2024-01-22 14:02:20","3,1","2,2"},
                // ── In-progress (1 move per turn, game still running) ──
                {1,1,"Alice",  13,"2024-01-23 10:00:00","2024-01-23 10:00:14","1,0","3,0"},
                {1,2,"Charlie",13,"2024-01-23 10:00:18","2024-01-23 10:00:34","7,0","5,0"},
                {1,1,"Bob",    14,"2024-01-23 11:00:00","2024-01-23 11:00:13","3,1","1,1"},
                {1,2,"Eve",    14,"2024-01-23 11:00:17","2024-01-23 11:00:31","5,1","7,1"},
        };

        String sql = """
            INSERT INTO MOVES (move_number, turn_id, start_time, end_time,
                               start_location, end_location)
            VALUES (?,
                (SELECT t.turn_id FROM TURNS t
                 WHERE t.turn_number = ?
                   AND t.player_id  = (SELECT player_id FROM PLAYERS WHERE username = ?)
                   AND t.game_id    = (SELECT game_id   FROM GAMES ORDER BY game_id LIMIT 1 OFFSET ?)),
                ?::TIMESTAMP, ?::TIMESTAMP, ?, ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] m : moves) {
                ps.setInt(1,    (int)    m[0]);
                ps.setInt(2,    (int)    m[1]);
                ps.setString(3, (String) m[2]);
                ps.setInt(4,    (int)    m[3]);
                ps.setString(5, (String) m[4]);
                ps.setString(6, (String) m[5]);
                ps.setString(7, (String) m[6]);
                ps.setString(8, (String) m[7]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
    public void clearDatabase() throws SQLException, ZarocDaoException {
        try (Connection conn = DaoUtils.createConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(
                    "TRUNCATE TABLE MOVES, TURNS, GAME_PARTICIPATION, GAMES, PLAYERS RESTART IDENTITY CASCADE"
            );

            System.out.println("[MockDataLoader] Database cleared.");
        }
    }
}
