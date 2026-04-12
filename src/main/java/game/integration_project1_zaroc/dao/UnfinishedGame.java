package game.integration_project1_zaroc.dao;

import game.integration_project1_zaroc.model.gameinfo.PawnColor;

import java.time.LocalDateTime;

public class UnfinishedGame {
    private int gameId;
    private String currentUserName;
    private String opponentName;
    private String currentPlayerPfp;
    private String opponentPfp;
    private LocalDateTime startTime;
    private PawnColor currentPlayerColor;
    private PawnColor opponentPlayerColor;

    public UnfinishedGame(int gameId, String currentUserName, String opponentName, String currentPlayerPfp, String opponentPfp, LocalDateTime startTime, PawnColor currentPlayerColor, PawnColor opponentPlayerColor) {
        this.gameId = gameId;
        this.currentUserName = currentUserName;
        this.opponentName = opponentName;
        this.currentPlayerPfp = currentPlayerPfp;
        this.opponentPfp = opponentPfp;
        this.startTime = startTime;
        this.currentPlayerColor = currentPlayerColor;
        this.opponentPlayerColor = opponentPlayerColor;
    }

    public int getGameId() {
        return gameId;
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public String getCurrentPlayerPfp() {
        return currentPlayerPfp;
    }

    public String getOpponentPfp() {
        return opponentPfp;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public PawnColor getCurrentPlayerColor() {
        return currentPlayerColor;
    }

    public PawnColor getOpponentPlayerColor() {
        return opponentPlayerColor;
    }
}
