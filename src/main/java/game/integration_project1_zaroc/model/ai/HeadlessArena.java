package game.integration_project1_zaroc.model.ai;

import game.integration_project1_zaroc.model.boardinfo.Pawn;
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

/**
 * Headless simulation environment that generates training data for a neural network.
 * Memory Leak Fixed: AI Models are reused instead of re-instantiated per game.
 *
 * @author Alessandro De Jongh
 * @version 2.2
 */
public class HeadlessArena {

    private static final int TOTAL_GAMES = 100000;
    private static final int MAX_TURNS_PER_GAME = 250;

    private static final int[] MAX_STACK_PER_ROW = {4, 3, 2, 1};

    private static final Difficulty AI_1_DIFFICULTY_ENUM = Difficulty.ELITE;
    private static final int AI_1_DIFFICULTY_INT = 3;
    private static final String AI_1_NAME = "SEBASTIAN";

    private static final Difficulty AI_2_DIFFICULTY_ENUM = Difficulty.ELITE;
    private static final int AI_2_DIFFICULTY_INT = 3;
    private static final String AI_2_NAME = "STEFAN";

    public static void main(String[] args) {
        System.out.println("Zaroc AI Arena: Deep Learning Data Collector");
        System.out.println("Matchup: [" + AI_1_NAME + "] vs [" + AI_2_NAME + "]");
        System.out.println("Target: " + TOTAL_GAMES + " games. Rules: 4-3-2-1 stacking.");

        int p1Wins = 0;
        int p2Wins = 0;
        int draws = 0;
        long startTime = System.currentTimeMillis();

        // 1. Maak de zware AI objecten slechts ÉÉN KEER aan om C++ memory leaks te voorkomen
        AIPlayer player1 = new AIPlayer(AI_1_DIFFICULTY_ENUM, AI_1_NAME);
        AIPlayer player2 = new AIPlayer(AI_2_DIFFICULTY_ENUM, AI_2_NAME);
        AiModel ai1 = new AiModel(AI_1_DIFFICULTY_INT, player1.getUsername());
        AiModel ai2 = new AiModel(AI_2_DIFFICULTY_INT, player2.getUsername());

        for (int i = 1; i <= TOTAL_GAMES; i++) {
            boolean p1Starts = (i % 2 != 0);
            int result = playSingleGame(p1Starts, i, player1, player2, ai1, ai2);

            if (result == 1) p1Wins++;
            else if (result == 2) p2Wins++;
            else draws++;

            System.out.println("\nFinished Game " + i + "/" + TOTAL_GAMES
                    + " | P1: " + p1Wins + " | P2: " + p2Wins + " | Draws: " + draws);

            // Forceer Java af en toe om rommel op te ruimen
            if (i % 50 == 0) System.gc();
        }

        long endTime = System.currentTimeMillis();
        double timeInSeconds = (endTime - startTime) / 1000.0;

        System.out.println("\n==================================");
        System.out.println("         FINAL RESULTS           ");
        System.out.println("==================================");
        System.out.printf("Total Time: %.2f sec%n", timeInSeconds);
        System.out.println("Data saved to: zaroc_training_data.csv");
    }

    private static int playSingleGame(boolean p1Starts, int gameNum, AIPlayer player1, AIPlayer player2, AiModel ai1, AiModel ai2) {
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
            System.out.print("\rGame " + gameNum + " progress: " + (turnCount + 1) + "/" + MAX_TURNS_PER_GAME);

            boolean isP1Turn = game.getCurrentTurn().getCurrentPlayer()
                    .getUsername().equals(player1.getUsername());

            gameHistory.add(new BoardSnapshot(extractBoardState(game), isP1Turn));

            Player currentPlayer = game.getCurrentTurn().getCurrentPlayer();
            boolean isPlayer1 = currentPlayer.getUsername().equals(player1.getUsername());

            // Haal de juiste (hergebruikte) AI op
            AiModel currentAi = isPlayer1 ? ai1 : ai2;
            AIPlayer currentAiPlayer = isPlayer1 ? player1 : player2;

            Turn bestTurn = currentAi.getBestTurn(game, currentAiPlayer);
            if (bestTurn == null) break;

            executeTurnOnLiveGame(game, bestTurn);
            if (game.getStatus() == GameStatus.ENDED) break;

            game.switchCurrentPlayer();
            turnCount++;
        }

        int winnerCode = 0;
        if (game.getParticipation1().getWinner()) winnerCode = 1;
        else if (game.getParticipation2().getWinner()) winnerCode = -1;

        saveGameDataToCSV(gameHistory, winnerCode, player1.getUsername());
        return (winnerCode == 1) ? 1 : (winnerCode == -1 ? 2 : 0);
    }

    private static String extractBoardState(Game game) {
        StringBuilder sb = new StringBuilder();
        Peg[][] allPegs = game.getBoard().getAllPegs();
        PawnColor p1Color = game.getParticipation1().getChosenPawnColor();

        for (int row = 0; row < allPegs.length; row++) {
            for (int col = 0; col < allPegs[row].length; col++) {
                Peg peg = allPegs[row][col];
                int maxStack = MAX_STACK_PER_ROW[row];
                int[] slots = new int[4];

                if (peg != null && !peg.getPawns().isEmpty()) {
                    List<Pawn> pawns = peg.getPawns();
                    for (int slot = 0; slot < pawns.size() && slot < 4; slot++) {
                        slots[slot] = pawns.get(slot).getPawnColor().equals(p1Color) ? 1 : -1;
                    }
                }
                for (int slot = 0; slot < 4; slot++) {
                    sb.append(slots[slot]).append(",");
                }
                int currentSize = (peg != null) ? peg.getPawns().size() : 0;
                int isFull = (currentSize >= maxStack) ? 1 : 0;
                sb.append(isFull).append(",");
            }
        }

        boolean isP1Turn = game.getCurrentTurn().getCurrentPlayer()
                .getUsername().equals(game.getParticipation1().getPlayer().getUsername());
        sb.append(isP1Turn ? "1" : "-1");

        return sb.toString();
    }

    private static void saveGameDataToCSV(List<BoardSnapshot> gameHistory, int winnerCode, String p1Username) {
        // BufferedWriter zorgt voor sneller opslaan, pw.flush() garandeert dat het direct op de SSD staat
        try (FileWriter fw = new FileWriter("zaroc_training_data.csv", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {

            for (BoardSnapshot snapshot : gameHistory) {
                int relativeLabel;
                if (winnerCode == 0) {
                    relativeLabel = 0;
                } else if (snapshot.isP1Turn) {
                    relativeLabel = winnerCode == 1 ? 1 : -1;
                } else {
                    relativeLabel = winnerCode == -1 ? 1 : -1;
                }
                pw.println(snapshot.boardState + "," + relativeLabel);
            }
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void executeTurnOnLiveGame(Game game, Turn aiTurn) {
        if (aiTurn.getFirstMove() != null && game.getStatus() == GameStatus.PLAYING) {
            applyMove(game, aiTurn.getFirstMove());
        }
        if (aiTurn.getSecondMove() != null && game.getStatus() == GameStatus.PLAYING) {
            applyMove(game, aiTurn.getSecondMove());
        }
    }

    private static void applyMove(Game game, Move move) {
        Peg realStart = game.getBoard().getPegPosition(
                move.getStartPeg().getYPosition(),
                move.getStartPeg().getXPosition());
        Peg realDest = game.getBoard().getPegPosition(
                move.getDestinationPeg().getYPosition(),
                move.getDestinationPeg().getXPosition());
        game.selectStartPeg(realStart);
        game.executeMove(realDest);
    }

    private static class BoardSnapshot {
        final String boardState;
        final boolean isP1Turn;
        BoardSnapshot(String boardState, boolean isP1Turn) {
            this.boardState = boardState;
            this.isP1Turn = isP1Turn;
        }
    }
}