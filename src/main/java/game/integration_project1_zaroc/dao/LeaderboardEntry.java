package game.integration_project1_zaroc.dao;

/**
 * Represents a single row in the leaderboard, holding statistics for one player.
 */
public class LeaderboardEntry {
    /** The player's current rank in the sorted leaderboard. */
    private int rank;
    /** The player's username. */
    private String username;
    /** Total number of games the player has played. */
    private int gamesPlayed;
    /** Total number of games the player has won. */
    private int wins;
    /** Total number of games the player has lost. */
    private int losses;
    /** Win percentage rounded to one decimal place (0.0–100.0). */
    private double winPercentage;
    /** Total time spent in games, in seconds. */
    private long totalPlayTimeSeconds;
    /** Average number of moves made per game, rounded to two decimal places. */
    private double avgMovesPerGame;
    /** Average time in seconds spent per move, rounded to two decimal places. */
    private double avgSecPerMove;
    /** Total score, equal to the total number of wins. */
    private int totalScore;
    /** Difficulty used to see if player is an AI or not.*/
    private String difficulty;


    /**
     * Creates a new LeaderboardEntry with all statistics populated.
     *
     * @param rank                 the player's rank in the current sort order
     * @param username             the player's display name
     * @param gamesPlayed          total games participated in
     * @param wins                 total games won
     * @param losses               total games lost
     * @param winPercentage        win rate as a percentage (0.0–100.0)
     * @param totalPlayTimeSeconds total time spent in games in seconds
     * @param avgMovesPerGame      average moves made per game
     * @param avgSecPerMove        average seconds spent per move
     * @param totalScore           total score
     * @param difficulty           difficult of an AI (null if human)
     */
    public LeaderboardEntry(int rank, String username,
                            int gamesPlayed, int wins, int losses,
                            double winPercentage, long totalPlayTimeSeconds,
                            double avgMovesPerGame, double avgSecPerMove,
                            int totalScore, String difficulty) {
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
        this.difficulty =difficulty;
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

    public String getDifficulty(){return difficulty;}

    /**
     * Updates the player's rank after re-sorting the leaderboard.
     *
     * @param rank the new rank to assign
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * Returns the total play time formatted as {@code HH:MM:SS}.
     *
     * @return formatted play time string
     */
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
