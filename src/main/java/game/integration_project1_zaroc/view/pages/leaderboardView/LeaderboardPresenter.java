package game.integration_project1_zaroc.view.pages.leaderboardview;


import game.integration_project1_zaroc.dao.LeaderboardDao;

import game.integration_project1_zaroc.dao.LeaderboardEntry;
//import game.integration_project1_zaroc.dao.MockDataLoader;
import game.integration_project1_zaroc.dao.MockDataLoader;
import game.integration_project1_zaroc.dao.ZarocDaoException;


import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.utils.Observer;
import game.integration_project1_zaroc.view.sharedlogic.NavigationService;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.application.Platform;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Presenter for the leaderboard screen.
 * Loads player statistics from the database on a background thread
 * and handles sorting by selected value.
 */
public class LeaderboardPresenter implements Observer {

    /** The leaderboard view this presenter manages. */
    private final LeaderboardView view;
    /** DAO used to fetch leaderboard entries from the database. */
    private final LeaderboardDao dao;
    /** The connection to the model.*/
    private AppController model;

    /**
     * Cached list of leaderboard entries loaded from the database.
     * Used to avoid having to fetch again when the user changes the sort option.
     */
    private List<LeaderboardEntry> cachedEntries = new ArrayList<>();

    /**
     * Creates a new LeaderboardPresenter, registers event handlers,
     * and triggers the initial leaderboard load.
     *
     * @param view          the leaderboard view to manage.
     * @param appController the connection to the model.
     */
    public LeaderboardPresenter(LeaderboardView view, AppController appController) {
        this.view = view;
        this.model =appController;
        this.dao  = new LeaderboardDao();
        view.getResourceManager().addObserver(this);
        attachEventHandlers();
        loadLeaderboard();
    }

    /**
     * Registers event handlers for the leaderboard view controls.
     * Attaches a close action, hover effect, and sound effect to the return button,
     * and a sort listener to the sort dropdown.
     */
    private void attachEventHandlers() {

        view.getReturnButton().setOnAction(actionEvent ->{
            NavigationService.closeWindow(this.view);
        });
        GeneralEventhandlers.addHoverEffect(view.getReturnButton());
        GeneralEventhandlers.addSoundEffect(view.getReturnButton(), view.getResourceManager());
        view.getSortDropdown().valueProperty().addListener(
                (obs, oldVal, newVal) -> applySortAndDisplay(newVal)
        );
        view.getAiDropdown().valueProperty().addListener((observableValue, oldVal, newVal) -> applySortAndDisplay(view.getSortDropdown().getValue()));
    }

    /**
     * Sorts the cached leaderboard entries by the given option and updates the view.
     * Assigns ranks after sorting. Does nothing if no entries are cached yet.
     *
     * @param sortOption the display name of the column to sort by, default is win rate.
     */
private void applySortAndDisplay(String sortOption) {
    if (cachedEntries.isEmpty()) return;
    List<LeaderboardEntry> entriesToSort = new ArrayList<>();

    String selectedValue = view.getAiDropdown().getValue();

    for(LeaderboardEntry leaderboardEntry: cachedEntries){
        if(selectedValue.equals("Only AI")){
            if(!(leaderboardEntry.getDifficulty()==null)){
                entriesToSort.add(leaderboardEntry);
            }
        }else if(selectedValue.equals("Only human players")){
            if(leaderboardEntry.getDifficulty()==null){
                entriesToSort.add(leaderboardEntry);
            }
        }else{
            entriesToSort.add(leaderboardEntry);
        }
    }

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

    List<LeaderboardEntry> sorted = new ArrayList<>(entriesToSort);
    sorted.sort(comparator);

    for (int i = 0; i < sorted.size(); i++) {
        sorted.get(i).setRank(i + 1);
    }

    view.setItems(sorted);
}

    /**
     * Loads leaderboard entries from the database on a background thread.
     * Enters mock data in the database if its empty.
     * Updates the view once loading completes.
     * Displays an error message in the view if the anything fails.
     */

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
                            : entries.size() + " player(s) found in total.");
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
    /**
     * Called when the resource manager notifies observers of a layout change.
     * Triggers a full layout refresh on the view.
     */
    @Override
    public void updateLayout(Object args) {
        view.layoutNodes();
    }

    }

