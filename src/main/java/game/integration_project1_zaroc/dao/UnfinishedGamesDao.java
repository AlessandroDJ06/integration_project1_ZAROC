package game.integration_project1_zaroc.dao;

import game.integration_project1_zaroc.model.gameinfo.PawnColor;
import game.integration_project1_zaroc.model.gamelogic.Move;
import game.integration_project1_zaroc.model.gamelogic.Turn;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UnfinishedGamesDao {
    public List<UnfinishedGame> fetchUnfinishedGames(int playerId) throws ZarocDaoException { // Exception toevoegen
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
                opt.profile_picture AS opponent_pfp
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
                            PawnColor.valueOf(rs.getString("opponent_color").toUpperCase())
                    ));
                }
            }
        } catch (java.sql.SQLException e) {
            throw new ZarocDaoException("Fout bij het ophalen van onafgewerkte spellen", e);
        }

        return games;
    }

}
