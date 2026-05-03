package game.integration_project1_zaroc.view.pages.unfinishedgameplayervplayer;

import game.integration_project1_zaroc.dao.UnfinishedGame;
import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.model.players.Player;
import game.integration_project1_zaroc.view.pages.playervsplayerview.PlayerVsPlayerPresenter;
import game.integration_project1_zaroc.view.pages.playervsplayerview.PlayerVsPlayerView;
import game.integration_project1_zaroc.view.sharedlogic.NavigationService;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Arrays;
import java.util.Objects;

public class UnfinishedGamePlayerVsPlayerSetupPresenter {
    private UnfinishedGamePlayerVsPlayerSetupView view;
    private AppController model;
    private UnfinishedGame playerTwo;

    public UnfinishedGamePlayerVsPlayerSetupPresenter(UnfinishedGamePlayerVsPlayerSetupView view, AppController model, UnfinishedGame playerTwo) {
        this.view = view;
        this.model = model;
        this.playerTwo = playerTwo;
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
            NavigationService.navigateToPlayerVsPlayerView(view.getResourceManager(), this.model, playerTwo);
            NavigationService.closeWindow(this.view);

        });

        view.getMultiplayerGame().setOnAction(event -> {
            model.setOnlineMultiplayer(true);
            model.setContinueInMultiplayer(true);
            model.setHost(true);
            NavigationService.closeWindow(this.view);
        });


    }
}
