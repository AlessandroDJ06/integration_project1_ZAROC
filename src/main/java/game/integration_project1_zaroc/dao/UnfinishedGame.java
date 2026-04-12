package game.integration_project1_zaroc.dao;

import game.integration_project1_zaroc.model.gameinfo.PawnColor;
import game.integration_project1_zaroc.model.players.Difficulty;

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
    private boolean oppIsAi;
    private boolean currIsAi;
    private Difficulty currDifficulty;
    private Difficulty oppDifficulty;
    private String currEmail;
    private String oppEmail;
    private int currId;
    private int oppId;


    public UnfinishedGame(int gameId, String currentUserName, String opponentName, String currentPlayerPfp, String opponentPfp, LocalDateTime startTime, PawnColor currentPlayerColor, PawnColor opponentPlayerColor, String currPassword, String oppPassword, String currDifficulty, String optDifficulty,String currEmail,String oppEmail,int currId, int oppId) {
        this.gameId = gameId;
        this.currentUserName = currentUserName;
        this.opponentName = opponentName;
        this.currentPlayerPfp = currentPlayerPfp;
        this.opponentPfp = opponentPfp;
        this.startTime = startTime;
        this.currentPlayerColor = currentPlayerColor;
        this.opponentPlayerColor = opponentPlayerColor;
        this.currIsAi = (currPassword == null);
        this.oppIsAi = (oppPassword == null);
        this.currDifficulty = (currDifficulty == null ? null : Difficulty.valueOf(currDifficulty) );
        this.oppDifficulty = (optDifficulty == null ? null : Difficulty.valueOf(optDifficulty));
        this.currEmail = currEmail;
        this.oppEmail = oppEmail;
        this.currId = currId;
        this.oppId = oppId;
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

    public boolean isOppIsAi() {
        return oppIsAi;
    }

    public boolean isCurrIsAi() {
        return currIsAi;
    }

    public Difficulty getCurrDifficulty() {
        return currDifficulty;
    }

    public Difficulty getOppDifficulty() {
        return oppDifficulty;
    }

    public String getCurrEmail() {
        return currEmail;
    }

    public String getOppEmail() {
        return oppEmail;
    }

    public int getCurrId() {
        return currId;
    }

    public int getOppId() {
        return oppId;
    }
}
