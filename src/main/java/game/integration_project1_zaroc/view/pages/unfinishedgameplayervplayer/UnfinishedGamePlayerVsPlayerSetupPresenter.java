package game.integration_project1_zaroc.view.pages.unfinishedgameplayervplayer;

import game.integration_project1_zaroc.dao.UnfinishedGame;
import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.utils.Observer;
import game.integration_project1_zaroc.view.sharedlogic.NavigationService;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.scene.control.Button;
import java.util.Arrays;

public class UnfinishedGamePlayerVsPlayerSetupPresenter implements Observer {
    private UnfinishedGamePlayerVsPlayerSetupView view;
    private AppController model;
    private UnfinishedGame playerTwo;

    public UnfinishedGamePlayerVsPlayerSetupPresenter(UnfinishedGamePlayerVsPlayerSetupView view, AppController model, UnfinishedGame playerTwo) {
        this.view = view;
        this.model = model;
        this.playerTwo = playerTwo;
        view.getResourceManager().addObserver(this);
        addEventHandlers();
    }

    private void addEventHandlers() {
        for (Button button : Arrays.asList(
                view.getReturnButton(),
                view.getLocalGame(),
                view.getMultiplayerGame())) {
            GeneralEventhandlers.addHoverEffect(button);
            GeneralEventhandlers.addSoundEffect(button, view.getResourceManager());
        }

        view.getReturnButton().setOnAction(event -> {
            NavigationService.closeWindow(this.view);
        });

        view.getLocalGame().setOnAction(event -> {
            model.setContinueInLocalPlayer(true);
            NavigationService.navigateToPlayerVsPlayerView(view.getResourceManager(), this.model, playerTwo).showAndWait();
            NavigationService.closeWindow(this.view);

        });

        view.getMultiplayerGame().setOnAction(event -> {
            model.setOnlineMultiplayer(true);
            model.setContinueInMultiplayer(true);
            model.setHost(true);
            NavigationService.closeWindow(this.view);
        });


    }

    @Override
    public void updateLayout(Object args) {
        view.layoutNodes();
    }
}
