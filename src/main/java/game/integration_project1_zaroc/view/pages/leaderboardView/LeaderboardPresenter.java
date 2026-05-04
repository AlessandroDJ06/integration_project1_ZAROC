package game.integration_project1_zaroc.view.pages.leaderboardview;


import game.integration_project1_zaroc.dao.LeaderboardDao;

import game.integration_project1_zaroc.dao.LeaderboardEntry;
import game.integration_project1_zaroc.dao.MockDataLoader;
import game.integration_project1_zaroc.dao.ZarocDaoException;


import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.utils.Observer;
import game.integration_project1_zaroc.view.sharedlogic.NavigationService;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import game.integration_project1_zaroc.view.pages.leaderboardview.LeaderboardView;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class LeaderboardPresenter implements Observer {

    private final LeaderboardView view;
    private final LeaderboardDao dao;
    private AppController model;


    private List<LeaderboardEntry> cachedEntries = new ArrayList<>();

    public LeaderboardPresenter(LeaderboardView view, AppController appController) {
        this.view = view;
        this.model =appController;
        this.dao  = new LeaderboardDao();
        view.getResourceManager().addObserver(this);
        attachEventHandlers();
        loadLeaderboard();
    }

    private void attachEventHandlers() {

        view.getReturnButton().setOnAction(actionEvent ->{
            NavigationService.closeWindow(this.view);
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

    List<LeaderboardEntry> sorted = new ArrayList<>(cachedEntries);
    sorted.sort(comparator);

    for (int i = 0; i < sorted.size(); i++) {
        sorted.get(i).setRank(i + 1);
    }

    view.setItems(sorted);
}

    public void loadLeaderboard(){
        view.setStatusText("Loading leaderboard…");
        try{
            new MockDataLoader().loadIfEmpty();
        }catch(SQLException|ZarocDaoException e){
            System.out.println("Mock data loader not working" + e.getMessage());
        }

        Thread dbThread = new Thread(() -> {
            try {
                List<LeaderboardEntry> entries = dao.fetchLeaderboard();
                cachedEntries=entries;

                Platform.runLater(() -> {
                    view.setItems(entries);
                    view.setStatusText(entries.isEmpty()
                            ? "No finished games found."
                            : entries.size() + " player(s) on the board.");
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

    @Override
    public void updateLayout(Object args) {
        view.layoutNodes();
    }

    }

