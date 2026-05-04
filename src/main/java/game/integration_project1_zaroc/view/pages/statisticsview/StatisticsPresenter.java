package game.integration_project1_zaroc.view.pages.statisticsview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.model.players.HumanPlayer;
import game.integration_project1_zaroc.utils.Observer;
import game.integration_project1_zaroc.view.sharedlogic.NavigationService;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.profilePictures.ProfilePictures;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.stage.Stage;

public class StatisticsPresenter implements Observer {
    private AppController model;
    private StatisticsView view;

    public StatisticsPresenter(StatisticsView view, AppController model) {
        this.view = view;
        this.model = model;
        view.getResourceManager().addObserver(this);
        updateStats();
        addEventHandlers();
    }


    private void addEventHandlers() {
        view.getReturnButton().setOnAction(event -> {
            NavigationService.closeWindow(this.view);
        });
        GeneralEventhandlers.addHoverEffect(view.getReturnButton());
    }


    private void updateStats() {

        HumanPlayer player = (HumanPlayer) model.getPlayer1();

        if (!model.isAllowedToUseDatabase()) {
            view.showGuestText();
            return;
        }
        String difficulty = model.getMostUsedDifficulty();
        String playstyle = "None";

        if(model.getGame() !=null) {
            playstyle = model.getGame().calculateGameStyle();
        }
        int totalGames = model.getTotalGamesPlayed();
        int totalWins = model.getTotalWins();
        double winRate = (totalGames == 0) ? 0 : (double) totalWins / totalGames * 100;
        view.getAccountInfo().setText("Username: " + player.getUsername() + "\nEmail: " + player.getEmail());



        String pic = player.getProfilePicture();
        if (pic != null) {
            view.getProfilePicture().setImage(view.getResourceManager().getProfilePicture(ProfilePictures.valueOf(pic)));
        }

        view.getPreferencesText().setText("Playstyle: " + playstyle + "\nMost Played Difficulty: " + difficulty);

        view.getGameStatsText().setText("Total Games: " + totalGames +
                "\nTotal Wins: " + totalWins +
                "\nWin Rate: " + String.format("%.1f", winRate) + "%");

    }

    @Override
    public void updateLayout(Object args) {
        view.layoutNodes();
    }


}

