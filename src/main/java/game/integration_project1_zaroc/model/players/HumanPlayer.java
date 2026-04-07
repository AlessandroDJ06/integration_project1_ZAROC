package game.integration_project1_zaroc.model.players;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HumanPlayer extends Player {
    private String email;

    public HumanPlayer(String username, String email) {
        super(username);
        this.email = email;
    }

    public static HumanPlayer fromResultSet(ResultSet rs) throws SQLException {
        HumanPlayer hp = new HumanPlayer(
                rs.getString("username"),
                rs.getString("email")
        );
        hp.setPlayerId(rs.getInt("player_id"));
        hp.setProfilePicture(rs.getString("profile_picture"));

        String style = rs.getString("play_style");
        if (style != null) {
            hp.setPlayerStyle(PlayerStyle.valueOf(style));
        }

        return hp;
    }

    public void setUsername(String username) {
        super.setUsername(username);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}