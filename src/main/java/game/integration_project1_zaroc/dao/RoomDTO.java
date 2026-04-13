package game.integration_project1_zaroc.dao;
import game.integration_project1_zaroc.model.gameinfo.PawnColor;

public class RoomDTO {
    private int roomId;
    private String roomCode;
    private int hostId;
    private int guestId;
    private int gameId;
    private PawnColor hostColor;
    private String status;

    public RoomDTO(int roomId, String roomCode, int hostId, int guestId, int gameId, PawnColor hostColor, String status) {
        this.roomId = roomId;
        this.roomCode = roomCode;
        this.hostId = hostId;
        this.guestId = guestId;
        this.gameId = gameId;
        this.hostColor = hostColor;
        this.status = status;
    }

    public int getRoomId() { return roomId; }
    public String getRoomCode() { return roomCode; }
    public int getHostId() { return hostId; }
    public int getGuestId() { return guestId; }
    public int getGameId() { return gameId; }
    public PawnColor getHostColor() { return hostColor; }
    public String getStatus() { return status; }
}
