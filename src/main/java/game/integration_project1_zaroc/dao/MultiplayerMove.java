package game.integration_project1_zaroc.dao;

import java.sql.Timestamp;

public class MultiplayerMove {
    private final int turnId;
    private final int moveNumber;
    private final Timestamp startTime;
    private final Timestamp endTime;
    private final String startLocation;
    private final String endLocation;
    private final String username;

    public MultiplayerMove(int turnId, int moveNumber, Timestamp startTime, Timestamp endTime, String startLocation, String endLocation, String username) {
        this.turnId = turnId;
        this.moveNumber = moveNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.username = username;
    }

    // Getters
    public int getTurnId() { return turnId; }
    public int getMoveNumber() { return moveNumber; }
    public Timestamp getStartTime() { return startTime; }
    public Timestamp getEndTime() { return endTime; }
    public String getStartLocation() { return startLocation; }
    public String getEndLocation() { return endLocation; }
    public String getUsername() { return username; }
}
