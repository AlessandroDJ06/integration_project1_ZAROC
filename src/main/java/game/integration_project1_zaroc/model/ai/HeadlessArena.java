package game.integration_project1_zaroc.model.ai;

import game.integration_project1_zaroc.model.boardinfo.Peg;
import game.integration_project1_zaroc.model.gameinfo.GameParticipation;
import game.integration_project1_zaroc.model.gameinfo.GameStatus;
import game.integration_project1_zaroc.model.gameinfo.PawnColor;
import game.integration_project1_zaroc.model.gamelogic.Game;
import game.integration_project1_zaroc.model.gamelogic.Move;
import game.integration_project1_zaroc.model.gamelogic.Turn;
import game.integration_project1_zaroc.model.players.AIPlayer;
import game.integration_project1_zaroc.model.players.Difficulty;
import game.integration_project1_zaroc.model.players.Player;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class HeadlessArena {

    private static final int    TOTAL_GAMES = 100000;
    private static final int    MAX_TURNS_PER_GAME = 300;
    private static final double GAMMA = 0.97;

    public static void main(String[] args) {

        AIPlayer p1 = new AIPlayer(Difficulty.ELITE, "speler1");
        AIPlayer p2 = new AIPlayer(Difficulty.ELITE, "speler2");

        int p1Wins = 0, p2Wins = 0, draws = 0;
        long startTime = System.currentTimeMillis();
        String fileName = "zaroc_training_" + startTime + ".csv";

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fileName, false)))) {

            for (int gameCounter = 0; gameCounter < TOTAL_GAMES; gameCounter++) {
                boolean p1Starts = (gameCounter % 2 == 0);
                int result = playSingleGame(p1Starts, p1, p2, pw);

                if      (result == 1) p1Wins++;
                else if (result == 2) p2Wins++;
                else                  draws++;

                System.out.println("Game " + (gameCounter + 1) + "/" + TOTAL_GAMES
                        + " | Winnaar: " + (result == 1 ? p1.getUsername() : result == 2 ? p2.getUsername() : "Gelijkspel"));

                if (gameCounter % 10 == 0) pw.flush();
            }

        } catch (IOException e) {
            System.err.println("Fout bij schrijven: " + e.getMessage());
        }

        long endTime = System.currentTimeMillis();
        System.out.printf("Total time: %.2f sec%n", (endTime - startTime) / 1000.0);
        System.out.printf("P1 wins: %d | P2 wins: %d | Draws: %d%n", p1Wins, p2Wins, draws);
        System.out.println("Data saved to: " + fileName);
    }


    private static int playSingleGame(boolean p1Starts, AIPlayer player1, AIPlayer player2, PrintWriter pw) {
        GameParticipation gp1 = new GameParticipation(player1, PawnColor.WHITE);
        GameParticipation gp2 = new GameParticipation(player2, PawnColor.BLACK);

        Game game = new Game(gp1, gp2);
        game.getBoard().setupStart();
        game.setAllowedSave(false);

        Player startingPlayer = p1Starts ? player1 : player2;
        game.startNewTurn(startingPlayer);

        List<BoardSnapshot> gameHistory = new ArrayList<>();
        int turnCount = 0;

        while (turnCount < MAX_TURNS_PER_GAME && game.getStatus() == GameStatus.PLAYING) {
            boolean isPlayer1 = game.getCurrentTurn().getCurrentPlayer()
                    .getUsername().equals(player1.getUsername());
            AIPlayer currentAiPlayer = isPlayer1 ? player1 : player2;

            Turn bestTurn = currentAiPlayer.getModel().getBestTurn(game, currentAiPlayer);
            if (bestTurn == null) break;

            gameHistory.add(new BoardSnapshot(extractBoardState(game), isPlayer1));

            executeTurnOnLiveGame(game, bestTurn);
            if (game.getStatus() == GameStatus.ENDED) break;

            game.switchCurrentPlayer();
            turnCount++;
        }

        int winnerCode = 0;
        if      (game.getParticipation1().getWinner()) winnerCode =  1;
        else if (game.getParticipation2().getWinner()) winnerCode = -1;

        saveGameDataToCSV(gameHistory, winnerCode, pw);
        return (winnerCode == 1) ? 1 : (winnerCode == -1 ? 2 : 0);
    }


    private static String extractBoardState(Game game) {
        float[] encoded = ZarocNeuralNet.encodeBoard(game);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < encoded.length; i++) {
            sb.append((int) encoded[i]);
            if (i < encoded.length - 1) sb.append(",");
        }
        return sb.toString();
    }


    private static void saveGameDataToCSV(List<BoardSnapshot> gameHistory, int winnerCode, PrintWriter pw) {
        int totalTurns = gameHistory.size();
        for (int i = 0; i < totalTurns; i++) {
            BoardSnapshot snapshot = gameHistory.get(i);

            int relativeLabel;
            if (winnerCode == 0) {
                relativeLabel = 0;
            } else if (snapshot.isP1Turn) {
                relativeLabel = winnerCode == 1 ? 1 : -1;
            } else {
                relativeLabel = winnerCode == -1 ? 1 : -1;
            }

            int turnsFromEnd = totalTurns - i;
            double discountedLabel = relativeLabel * Math.pow(GAMMA, turnsFromEnd);

            pw.println(snapshot.boardState + "," + discountedLabel);
        }
    }

    private static void executeTurnOnLiveGame(Game game, Turn turn) {
        if (turn.getFirstMove() != null && game.getStatus() == GameStatus.PLAYING)
            applyMove(game, turn.getFirstMove());
        if (turn.getSecondMove() != null && game.getStatus() == GameStatus.PLAYING)
            applyMove(game, turn.getSecondMove());
    }

    private static void applyMove(Game game, Move move) {
        Peg realStart = game.getBoard().getPegPosition(
                move.getStartPeg().getYPosition(), move.getStartPeg().getXPosition());
        Peg realDest = game.getBoard().getPegPosition(
                move.getDestinationPeg().getYPosition(), move.getDestinationPeg().getXPosition());
        game.selectStartPeg(realStart);
        game.executeMove(realDest);
    }

    private static class BoardSnapshot {
        final String boardState;
        final boolean isP1Turn;

        BoardSnapshot(String boardState, boolean isP1Turn) {
            this.boardState = boardState;
            this.isP1Turn   = isP1Turn;
        }
    }
}