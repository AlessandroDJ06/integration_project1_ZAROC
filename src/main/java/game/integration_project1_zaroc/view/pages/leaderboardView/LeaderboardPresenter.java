package game.integration_project1_zaroc.view.pages.leaderboardview;


import game.integration_project1_zaroc.dao.LeaderboardDao;

import game.integration_project1_zaroc.dao.LeaderboardEntry;
import game.integration_project1_zaroc.dao.MockDataLoader;
import game.integration_project1_zaroc.dao.ZarocDaoException;


import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import game.integration_project1_zaroc.view.pages.leaderboardview.LeaderboardView;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class LeaderboardPresenter {

    private final LeaderboardView view;
    private final LeaderboardDao dao;
    private AppController model;

    private List<LeaderboardEntry> cachedEntries = new ArrayList<>();

    public LeaderboardPresenter(LeaderboardView view, AppController appController) {
        this.view = view;
        this.model =appController;
        this.dao  = new LeaderboardDao();
        attachEventHandlers();
        loadLeaderboard();
    }

    private void attachEventHandlers() {

        view.getReturnButton().setOnAction(actionEvent ->{
            Stage stage = (Stage) view.getScene().getWindow();
            stage.close();
        });
        GeneralEventhandlers.addHoverEffect(view.getReturnButton());
        GeneralEventhandlers.addSoundEffect(view.getReturnButton(), view.getResourceManager());
        view.getSortDropdown().valueProperty().addListener(
                (obs, oldVal, newVal) -> applySortAndDisplay(newVal)
        );
    }


    private void applySortAndDisplay(String sortOption) {
        if (cachedEntries.isEmpty()) return;

        Comparator<LeaderboardEntry> comparator = switch (sortOption) {
            case "Wins"            -> Comparator.comparingInt(LeaderboardEntry::getWins).reversed();
            case "Losses"          -> Comparator.comparingInt(LeaderboardEntry::getLosses).reversed();
            case "Games Played"    -> Comparator.comparingInt(LeaderboardEntry::getGamesPlayed).reversed();
            case "Total Play Time" -> Comparator.comparingLong(LeaderboardEntry::getTotalPlayTimeSeconds).reversed();
            case "Avg Moves / Game"-> Comparator.comparingDouble(LeaderboardEntry::getAvgMovesPerGame).reversed();
            case "Avg Sec / Move"  -> Comparator.comparingDouble(LeaderboardEntry::getAvgSecPerMove).reversed();
            case "Total Score"     -> Comparator.comparingInt(LeaderboardEntry::getTotalScore).reversed();
            default                -> // "Win Rate" — default
                    Comparator.comparingDouble(LeaderboardEntry::getWinPercentage)
                            .thenComparingInt(LeaderboardEntry::getWins).reversed();
        };

        // Sort a copy so the original order is preserved for future sorts
        List<LeaderboardEntry> sorted = new ArrayList<>(cachedEntries);
        sorted.sort(comparator);

        // Reassign rank numbers to reflect the new order
        for (int i = 0; i < sorted.size(); i++) {
            sorted.get(i).setRank(i + 1);
        }

        view.setItems(formatEntries(sorted));
    }

    public void loadLeaderboard(){
        view.setStatusText("Loading leaderboard…");
       // load mock data if db empty
        try{
            new MockDataLoader().loadIfEmpty();
        }catch(SQLException|ZarocDaoException e){
            System.out.println("Mock data loader not working" + e.getMessage());
        }

        Thread dbThread = new Thread(() -> {
            try {
                List<LeaderboardEntry> entries = dao.fetchLeaderboard();
                cachedEntries=entries;
                List<String> rows = formatEntries(entries);

                Platform.runLater(() -> {
                    view.setItems(rows);
                    view.setStatusText(rows.isEmpty()
                            ? "No finished games found."
                            : rows.size() + " player(s) on the board.");
                });

            } catch (SQLException | ZarocDaoException e) {
                Platform.runLater(() ->
                        view.setStatusText("Could not load data: " + e.getMessage())
                );
                System.err.println("[LeaderboardPresenter] DB error: " + e.getMessage());
            }
        }, "leaderboard-db-thread");

        dbThread.setDaemon(true);
        dbThread.start();
    }


    private List<String> formatEntries(List<LeaderboardEntry> entries) {

        List<String> rows = new ArrayList<>();

        for (LeaderboardEntry e : entries) {


            String row = String.format(
                    "#%s %-18s | Played: %2d | W: %4d | L: %4d | Win%%: %5.1f%% | " +
                            "Time: %s | Avg Moves: %5.1f | Avg s/Move: %5.1f | Score: %3d",
                    e.getRank(),
                    e.getUsername(),
                    e.getGamesPlayed(),
                    e.getWins(),
                    e.getLosses(),
                    e.getWinPercentage(),
                    e.getFormattedPlayTime(),
                    e.getAvgMovesPerGame(),
                    e.getAvgSecPerMove(),
                    e.getTotalScore()
            );
            rows.add(row);
        }

        return rows;
    }
}
