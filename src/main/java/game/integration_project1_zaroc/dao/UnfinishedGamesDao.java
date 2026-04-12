package game.integration_project1_zaroc.dao;

import game.integration_project1_zaroc.model.gameinfo.PawnColor;
import game.integration_project1_zaroc.model.gamelogic.Move;
import game.integration_project1_zaroc.model.gamelogic.Turn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UnfinishedGamesDao {
    public List<UnfinishedGame> fetchUnfinishedGames(int playerId) throws ZarocDaoException {
        List<UnfinishedGame> games = new ArrayList<>();
        String sql = """ 
                SELECT
                p1.game_id,
                g.start_time,
                p1.player_id AS my_id,
                curr.username AS my_name,
                p1.pawn_color AS my_color,
                curr.profile_picture AS my_pfp,
                p2.player_id AS opponent_id,
                opt.username AS opponent_name,
                p2.pawn_color AS opponent_color,
                opt.profile_picture AS opponent_pfp,
                curr.password AS curr_passwd,
                opt.password AS opt_passwd,
                curr.difficulty AS curr_difficulty,
                opt.difficulty AS opt_difficulty,
                curr.email AS curr_email,
                opt.email AS opt_email
                
                FROM game_participation p1
                     JOIN games g ON p1.game_id = g.game_id
                     JOIN game_participation p2 ON p1.game_id = p2.game_id AND p1.player_id <> p2.player_id
                     JOIN players opt ON p2.player_id = opt.player_id
                     JOIN players curr ON p1.player_id = curr.player_id
                WHERE p1.player_id = ?
                AND upper(g.game_status) != 'ENDED';
                """;

        try (java.sql.Connection conn = DaoUtils.createConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, playerId);

            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    games.add(new UnfinishedGame(
                            rs.getInt("game_id"),
                            rs.getString("my_name"),
                            rs.getString("opponent_name"),
                            rs.getString("my_pfp"),
                            rs.getString("opponent_pfp"),
                            rs.getTimestamp("start_time").toLocalDateTime(),
                            PawnColor.valueOf(rs.getString("my_color").toUpperCase()),
                            PawnColor.valueOf(rs.getString("opponent_color").toUpperCase()),
                            rs.getString("curr_passwd"),
                            rs.getString("opt_passwd"),
                            rs.getString("curr_difficulty"),
                            rs.getString("opt_difficulty"),
                            rs.getString("curr_email"),
                            rs.getString("opt_email"),
                            rs.getInt("my_id"),
                            rs.getInt("opponent_id")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new ZarocDaoException("Fout bij het ophalen van onafgewerkte spellen", e);
        }

        return games;
    }

    public List<MovesUnfinishedGame> fetchMovesUnfinishedGame(int gameId) throws ZarocDaoException{
        List<MovesUnfinishedGame> movesUnfinishedGame = new ArrayList<>();

        String sql = """
                SELECT m.turn_id AS turn_id,
                       m.move_id AS move_id,
                       m.move_number AS move_number,
                       m.start_time AS start_time,
                       m.end_time AS end_time,
                       m.start_location AS start_location,
                       m.end_location AS end_location,
                       p.username AS username
                       
                FROM moves m
                         JOIN turns t ON m.turn_id = t.turn_id
                         JOIN players p ON t.player_id = p.player_id
                WHERE t.game_id = ?
                ORDER BY m.move_id ASC;
                """;

        try (java.sql.Connection conn = DaoUtils.createConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gameId);

            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    movesUnfinishedGame.add(new MovesUnfinishedGame(
                            rs.getInt("turn_id"),
                            rs.getInt("move_id"),
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
                throw new ZarocDaoException("fout bij het ophalen", e);
            }


            return movesUnfinishedGame;
    }


}
