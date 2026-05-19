package game.integration_project1_zaroc.dao;

import game.integration_project1_zaroc.model.gameinfo.PawnColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RoomDao {

    public void createRoom(String roomCode, int hostId, PawnColor hostColor) throws ZarocDaoException {
        String sql = "INSERT INTO GAME_ROOMS (room_code, host_id, status, host_color) VALUES (?, ?, 'WAITING', ?)";
        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomCode);
            ps.setInt(2, hostId);
            ps.setString(3, hostColor.name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ZarocDaoException("Kon de kamer niet aanmaken.", e);
        }
    }

    public void setGuestColor(String roomCode,int guestId,PawnColor pawnColor)throws ZarocDaoException{
        String sql = "UPDATE GAME_ROOMS SET guest_color = ? WHERE room_code = ? AND guest_id = ? ";

        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, pawnColor.name());
            ps.setString(2,roomCode);
            ps.setInt(3,guestId);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ZarocDaoException("kleur kon niet gezet worden",e);
        }
    }

    public void setHostColor(String roomCode, int hostId, PawnColor pawnColor)throws ZarocDaoException{
        String sql = "UPDATE GAME_ROOMS SET host_color = ? WHERE room_code = ? AND host_id = ? ";

        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, pawnColor.name());
            ps.setString(2,roomCode);
            ps.setInt(3, hostId);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ZarocDaoException("kleur kon niet gezet worden",e);
        }
    }

    public void joinRoom(String roomCode, int guestId) throws ZarocDaoException {
        String sql = "UPDATE GAME_ROOMS SET guest_id = ? WHERE room_code = ? AND status = 'WAITING'";
        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, guestId);
            ps.setString(2, roomCode);
            int updatedRows = ps.executeUpdate();
            if (updatedRows == 0) {
                throw new ZarocDaoException("Kamer niet gevonden of al vol!");
            }
        } catch (SQLException e) {
            throw new ZarocDaoException("Fout tijdens joinen van kamer.", e);
        }
    }

    public RoomDTO getRoomByCode(String roomCode) throws ZarocDaoException {
        String sql = "SELECT * FROM GAME_ROOMS WHERE room_code = ?";
        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PawnColor hostColor = PawnColor.valueOf(rs.getString("host_color"));
                    PawnColor guestColor = PawnColor.valueOf(rs.getString("guest_color"));
                    return new RoomDTO(
                            rs.getInt("room_id"),
                            rs.getString("room_code"),
                            rs.getInt("host_id"),
                            rs.getInt("guest_id"),
                            rs.getInt("game_id"),
                            hostColor,
                            guestColor,
                            rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            throw new ZarocDaoException("Kon kamer info niet ophalen.", e);
        }
        return null;
    }

    public int startGame(int roomId) throws ZarocDaoException {
        String gameSql = "INSERT INTO GAMES (start_time, game_status) VALUES (CURRENT_TIMESTAMP, 'PLAYING')";
        int newGameId = -1;

        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement psGame = conn.prepareStatement(gameSql, Statement.RETURN_GENERATED_KEYS)) {

            psGame.executeUpdate();
            try (ResultSet rs = psGame.getGeneratedKeys()) {
                if (rs.next()) {
                    newGameId = rs.getInt(1);
                } else {
                    throw new ZarocDaoException("Geen Game ID gegenereerd.");
                }
            }


            String roomSql = "UPDATE GAME_ROOMS SET game_id = ?, status = 'PLAYING' WHERE room_id = ?";
            try (PreparedStatement psRoom = conn.prepareStatement(roomSql)) {
                psRoom.setInt(1, newGameId);
                psRoom.setInt(2, roomId);
                psRoom.executeUpdate();
            }


            return newGameId;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ZarocDaoException("Kon het online spel niet starten in de database.", e);
        }
    }

    public void updateGameStatus(String status , String roomCode) throws ZarocDaoException{
        String sql = "UPDATE GAME_ROOMS SET status = ? WHERE room_code = ?";
        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,status);
            ps.setString(2,roomCode);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("De échte SQL fout is: " + e.getMessage());
            throw new ZarocDaoException("kon status niet updaten",e);
        }
    }

    public void removePlayer(String roomCode) throws ZarocDaoException{
        String sql = "UPDATE GAME_ROOMS SET guest_id = null , status = 'WAITING' WHERE room_code = ?";
        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
             ps.setString(1,roomCode);
             ps.executeUpdate();
        } catch (SQLException e) {
            throw new ZarocDaoException("kon speler niet updaten",e);
        }
    }

    public boolean isPlayerInGame(int gameId, int playerId) throws ZarocDaoException {
        String sql = "SELECT 1 FROM GAME_PARTICIPATION WHERE game_id = ? AND player_id = ?";
        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gameId);
            ps.setInt(2, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new ZarocDaoException("Kon niet controleren of speler in game zit", e);
        }
    }

    public void resumeGameInRoom(String roomCode, int existingGameId) throws ZarocDaoException {
        String sql = "UPDATE GAME_ROOMS SET game_id = ? WHERE room_code = ?";
        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, existingGameId);
            ps.setString(2, roomCode);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ZarocDaoException("Kon game niet hervatten in room", e);
        }
    }

    public void deleteRoom(String roomCode) throws ZarocDaoException{
        String sql = "DELETE FROM GAME_ROOMS WHERE room_code = ?";
        try (Connection conn = DaoUtils.createConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomCode);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ZarocDaoException("Kon gameroom niet verwijderen", e);
        }
    }
}