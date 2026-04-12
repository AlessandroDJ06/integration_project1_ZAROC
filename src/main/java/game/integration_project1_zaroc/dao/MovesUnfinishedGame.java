package game.integration_project1_zaroc.dao;

import game.integration_project1_zaroc.model.gamelogic.MoveNumber;

import java.sql.Timestamp;

public class MovesUnfinishedGame {
    private int turnId;
    private int moveId;
    private MoveNumber moveNumber;
    private Timestamp startTime;
    private Timestamp endTime;
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private String username;

    public MovesUnfinishedGame(int turnId, int moveId, int moveNumber, Timestamp startTime, Timestamp endTime,String startLocation,String endLocation,String username){
        this.turnId = turnId;
        this.moveId = moveId;
        this.moveNumber = MoveNumber.values()[moveNumber - 1];
        this.startTime = startTime;
        this.endTime = endTime;
        String[] startCoordinates = startLocation.split(",");
        this.startX = Integer.parseInt(startCoordinates[0]);
        this.startY = Integer.parseInt(startCoordinates[1]);
        String[] endCoordinates = endLocation.split(",");
        this.endX = Integer.parseInt(endCoordinates[0]);
        this.endY = Integer.parseInt(endCoordinates[1]);
        this.username = username;
    }

    public int getTurnId() {
        return turnId;
    }

    public String getUsername() {
        return username;
    }

    public int getMoveId() {
        return moveId;
    }

    public MoveNumber getMoveNumber() {
        return moveNumber;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }
}
