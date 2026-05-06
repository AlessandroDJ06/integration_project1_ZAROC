package game.integration_project1_zaroc.view.pages.selectgamemodeview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.utils.Observer;
import game.integration_project1_zaroc.view.sharedlogic.NavigationService;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.scene.control.Button;
import java.util.Arrays;

public class SelectGamemodePresenter implements Observer {
    private SelectGamemodeView view;
    private AppController model;

    public SelectGamemodePresenter(AppController model, SelectGamemodeView view) {
        this.view = view;
        view.getResourceManager().addObserver(this);
        this.model = model;
        addEventHandlers();
    }

    private void addEventHandlers() {
        for (Button button : Arrays.asList(
                view.getInfoButton(),
                view.getSettingsButton(),
                view.getProfileButton(),
                view.getPlayerVsAiButton(),
                view.getPlayerVsPlayerButton(),
                view.getUnfinishedGamesButton(),
                view.getMultiPlayerButton()
        )) {
            GeneralEventhandlers.addHoverEffect(button);
            GeneralEventhandlers.addSoundEffect(button, view.getResourceManager());
        }

        view.getSettingsButton().setOnAction(actionEvent -> {
            NavigationService.navigateToSettings(view.getResourceManager(), this.model).showAndWait();
        });

        view.getInfoButton().setOnAction(event -> {
            NavigationService.navigateToRules(view.getResourceManager(), this.model).showAndWait();
        });

        view.getPlayerVsAiButton().setOnAction(event -> {
            NavigationService.navigateToPlayerVsAiView(view.getResourceManager(), this.model).showAndWait();
            if (model.getPlayer2() != null) {
                NavigationService.navigateToGameSetup(view.getResourceManager(), this.model);
            }
        });

        view.getPlayerVsPlayerButton().setOnAction(event -> {
            NavigationService.navigateToPlayerVsPlayerView(view.getResourceManager(), this.model,null).showAndWait();
            if (model.getPlayer2() != null) {
                NavigationService.navigateToGameSetup(view.getResourceManager(), this.model);
            }
        });

        view.getUnfinishedGamesButton().setOnAction(event -> {
            NavigationService.navigateToUnfinishedGames(view.getResourceManager(), this.model).showAndWait();
            if (model.isContinueInMultiplayer() || model.getPlayer2() != null) {
                checkIfGameIsEmpty();
            }
        });

        view.getMultiPlayerButton().setOnAction(event -> {
            NavigationService.navigateToMultiplayerView(view.getResourceManager(), this.model).showAndWait();
            if (model.isOnlineMultiplayer()) {
                if (model.isHost()) {
                    NavigationService.navigateToMultiplayerHostView(view.getResourceManager(), this.model, -1).showAndWait();
                    checkIfGameIsEmpty();
                } else {
                    NavigationService.navigateToMultiplayerGuestView(view.getResourceManager(), this.model).showAndWait();
                    checkIfGameIsEmpty();
                }
            }
        });

        view.getProfileButton().setOnAction(event -> {
            NavigationService.navigateToStatisticsView(view.getResourceManager(), this.model).showAndWait();
        });

    }

    private void checkIfGameIsEmpty() {
        if (model.getGame() != null) {
            NavigationService.navigateToGameBoard(view.getResourceManager(), this.model);
        }
    }

    @Override
    public void updateLayout(Object args) {
        view.layoutNodes();
    }

}
