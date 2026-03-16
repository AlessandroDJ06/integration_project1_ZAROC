package game.integration_project1_zaroc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlayersDao {

    public void createPlayer(String naam,String email){
        //gen player_id
        //playstyle ??
        //set name
        //set email
        //set difficulty human

        String sql = "CREATE  ";//TODO

        try (Connection connection = DaoUtils.createConnection();
             PreparedStatement ps = DaoUtils.createPreparedStatement(connection, sql)) {

            //set values
            ps.execute();
        } catch (SQLException e) {
            throw new ZarocDaoException("Oopsie, couldn't create new player",e);
        }
    }





}
