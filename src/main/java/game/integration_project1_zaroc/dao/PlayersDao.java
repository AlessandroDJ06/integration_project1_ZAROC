package game.integration_project1_zaroc.dao;

import game.integration_project1_zaroc.model.players.AIPlayer;
import game.integration_project1_zaroc.model.players.HumanPlayer;
import game.integration_project1_zaroc.model.players.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlayersDao {

    public int createHumanPlayer(HumanPlayer player) throws ZarocDaoException {

        String sql = "INSERT INTO PLAYERS (username, email, play_style) VALUES (?, ?, NULL)";

        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, player.getUsername());
            ps.setString(2, player.getEmail());
            //ps.setString(3, player.getPlayerStyle().toString());
            //Paswoord etc. nog doen als die views worden gemaakt

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

    public int createAiPlayer(AIPlayer player) throws ZarocDaoException {
        System.out.println("ai speler");
        String sql = "INSERT INTO PLAYERS (username, email, play_style) VALUES (?, NULL, NULL)";

        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, player.getUsername());
            //ps.setString(3, player.getPlayerStyle().toString());

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

    public void updatePlayerPlaystyle(Player player) throws ZarocDaoException{
        String sql = "UPDATE PLAYERS SET playstyle = ? WHERE player_id = ?";


        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,player.getPlayerStyle().toString());
            ps.setInt(2,player.getPlayerId());

        } catch (SQLException sqlException){
            throw new ZarocDaoException("Kon de speler niet updaten.", sqlException);
    }





}}
