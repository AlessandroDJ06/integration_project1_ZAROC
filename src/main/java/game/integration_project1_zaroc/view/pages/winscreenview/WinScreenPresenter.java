package game.integration_project1_zaroc.view.pages.winscreenview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.model.gameinfo.GameStatus;
import game.integration_project1_zaroc.view.pages.gamesetupview.GameSetupPresenter;
import game.integration_project1_zaroc.view.pages.gamesetupview.GameSetupView;
import game.integration_project1_zaroc.view.pages.startview.StartPresenter;
import game.integration_project1_zaroc.view.pages.startview.StartView;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.stage.Stage;

public class WinScreenPresenter {
    private AppController model;
    private WinScreenView view;



    public WinScreenPresenter(WinScreenView view,AppController model){
        this.view = view;
        this.model = model;
        updateInfo();
        addEventHandlers();
    }

    private void addEventHandlers() {
        view.getReturnButton().setOnAction(event -> {
            Stage menuStage = (Stage) ((Stage) view.getScene().getWindow()).getOwner();
            StartView startView = new StartView(view.getResourceManager());
            new StartPresenter(model, startView);
            menuStage.getScene().setRoot(startView);

            closeWindow();
        });
        GeneralEventhandlers.addHoverEffect(view.getReturnButton());
        GeneralEventhandlers.addSoundEffect(view.getReturnButton(), view.getResourceManager());

        view.getRematchButton().setOnAction(event -> {

            Stage gameSetupStage = (Stage) ((Stage) view.getScene().getWindow()).getOwner();
            GameSetupView gameSetupView = new GameSetupView(view.getResourceManager());
            new GameSetupPresenter(gameSetupView, model);
            gameSetupStage.getScene().setRoot(gameSetupView);
            closeWindow();

        });
        GeneralEventhandlers.addHoverEffect(view.getRematchButton());
        GeneralEventhandlers.addSoundEffect(view.getRematchButton(), view.getResourceManager());

    }
    public void closeWindow(){
        Stage stage = (Stage) view.getScene().getWindow();
        stage.close();
    }
    public void updateInfo() {
        if (model.getGame().getStatus() != GameStatus.ENDED) return;


        int totalMoves = model.getGame().countTotalMoves();
        int yourMoves = model.getGame().countPlayerMoves(model.getPlayer1());
        int totalTurns = model.getGame().getTurns().size();
        int yourTurns = model.getGame().countPlayerTurns(model.getPlayer1());
        double avgTurnDuration = model.getGame().calculateTotalAvgTurnDuration();
        double avgPlayerTurnDuration = model.getGame().calculatePlayerAvgTurnDuration(model.getPlayer1());
        double avgMoveDuration = model.getGame().calculateTotalAvgMoveDuration();
        double avgPlayerMoveDuration = model.getGame().calculatePlayerAvgMoveDuration(model.getPlayer1());
        double durationSeconds = model.getGame().calculateGameDuration();

        String style = model.getGame().calculateGameStyle();

        view.getPlayerWon().setText(model.getGame().getWinner().getUsername().toUpperCase() + " WON!");


        String gameStats = String.format(
                "Duration: %dm %ds\n" +
                        "Total turns: %d\n" +
                        "Avg turn time: %.1fs\n" +
                        "Total moves: %d\n" +
                        "Avg move time: %.1fs",
                (int)(durationSeconds / 60), (int)(durationSeconds % 60),
                totalTurns,
                avgTurnDuration,
                totalMoves,
                avgMoveDuration
        );

        String playerStats = String.format(
                "Style: %s\n" +
                        "%d\n" +
                        "%.1fs\n" +
                        "%d\n" +
                        "%.1fs",
                style,
                yourTurns,
                avgPlayerTurnDuration,
                yourMoves,
                avgPlayerMoveDuration
        );

        view.getGameStats().setText(gameStats);
        view.getPlayerStats().setText(playerStats);
    }

    }

