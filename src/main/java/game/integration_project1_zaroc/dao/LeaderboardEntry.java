package game.integration_project1_zaroc.dao;


public class LeaderboardEntry {

    private int rank;
    private String username;
    private int gamesPlayed;
    private int wins;
    private int losses;
    private double winPercentage;
    private long totalPlayTimeSeconds;
    private double avgMovesPerGame;
    private double avgSecPerMove;
    private int totalScore;


    public LeaderboardEntry(int rank, String username,
                            int gamesPlayed, int wins, int losses,
                            double winPercentage, long totalPlayTimeSeconds,
                            double avgMovesPerGame, double avgSecPerMove,
                            int totalScore) {
        this.rank = rank;
        this.username = username;
        this.gamesPlayed = gamesPlayed;
        this.wins = wins;
        this.losses = losses;
        this.winPercentage = winPercentage;
        this.totalPlayTimeSeconds = totalPlayTimeSeconds;
        this.avgMovesPerGame = avgMovesPerGame;
        this.avgSecPerMove = avgSecPerMove;
        this.totalScore = totalScore;
    }


    public int getRank() {
        return rank;
    }

    public String getUsername() {
        return username;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public double getWinPercentage() {
        return winPercentage;
    }

    public long getTotalPlayTimeSeconds() {
        return totalPlayTimeSeconds;
    }

    public double getAvgMovesPerGame() {
        return avgMovesPerGame;
    }

    public double getAvgSecPerMove() {
        return avgSecPerMove;
    }

    public int getTotalScore() {
        return totalScore;
    }


    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getFormattedPlayTime() {
        long hours = totalPlayTimeSeconds / 3600;
        long minutes = (totalPlayTimeSeconds % 3600) / 60;
        long seconds = totalPlayTimeSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }


    @Override
    public String toString() {
        return String.format(
                "#%-4d | %-20s | Played: %3d | W: %3d | L: %3d | Win%%: %5.1f%% | " +
                        "Time: %s | Avg Moves: %5.1f | Avg s/Move: %5.1f | Score: %4d",
                rank,
                username,
                gamesPlayed,
                wins,
                losses,
                winPercentage,
                getFormattedPlayTime(),
                avgMovesPerGame,
                avgSecPerMove,
                totalScore
        );
    }
}
