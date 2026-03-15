package game.integration_project1_zaroc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DaoUtils {

    public static void loadDriver() {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (ClassNotFoundException e) {
            throw new ZarocDaoException("Fatal error: cannot load database driver", e);
        }
    }

    public static Connection createConnection() {
        try {                        //TODO:juiste databank opgeven met paswoord en user
            Connection connection = DriverManager.getConnection("TODO","sa", "");
            return connection;
        } catch (SQLException e){
            throw new ZarocDaoException("Cannot create connection with database", e);
        }
    }

    public static PreparedStatement createPreparedStatement(Connection connection, String sql) {
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new ZarocDaoException("Cannot create prepared statement", e);
        }
    }

        //Gebruik try with resources om automatisch alles af te sluiten (connection en statements)


    private void createTable() {
        String sql = ""; //TODO: create tabel DDL hier

        try (Connection connection = DaoUtils.createConnection();
                PreparedStatement ps = DaoUtils.createPreparedStatement(connection, sql)) {
            ps.execute();
        } catch (SQLException e) {
            throw new ZarocDaoException("Oopsie, something went wrong",e);
        }
    }

}
