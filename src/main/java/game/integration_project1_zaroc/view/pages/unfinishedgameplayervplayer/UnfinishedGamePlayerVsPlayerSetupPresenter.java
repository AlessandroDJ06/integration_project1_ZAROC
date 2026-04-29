package game.integration_project1_zaroc.view.pages.unfinishedgameplayervplayer;

import game.integration_project1_zaroc.dao.UnfinishedGame;
import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.model.players.Player;
import game.integration_project1_zaroc.view.pages.playervsplayerview.PlayerVsPlayerPresenter;
import game.integration_project1_zaroc.view.pages.playervsplayerview.PlayerVsPlayerView;
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

    public UnfinishedGamePlayerVsPlayerSetupPresenter(UnfinishedGamePlayerVsPlayerSetupView view, AppController model , UnfinishedGame playerTwo) {
        this.view = view;
        this.model = model;
        this.playerTwo = playerTwo;
        addEventHandlers();
    }

    private void addEventHandlers(){
        for (Button button : Arrays.asList(
                view.getReturnButton(),
                view.getLocalGame(),
                view.getMultiplayerGame())){
            GeneralEventhandlers.addHoverEffect(button);
            GeneralEventhandlers.addSoundEffect(button, view.getResourceManager());
        }

        view.getReturnButton().setOnAction(event -> {
            closeWindow();
        });

        view.getLocalGame().setOnAction(event -> {
            PlayerVsPlayerView playerVsPlayerView = new PlayerVsPlayerView(view.getResourceManager());
            new PlayerVsPlayerPresenter(playerVsPlayerView, model,playerTwo);
            model.setContinueInLocalPlayer(true);

            Scene playerVsPlayerScene = new Scene(playerVsPlayerView, 900, 750);
            playerVsPlayerScene.setFill(Color.TRANSPARENT);

            Stage playerVsPlayerStage = new Stage();
            playerVsPlayerStage.setScene(playerVsPlayerScene);
            playerVsPlayerStage.setTitle("Speler vs Speler");
            playerVsPlayerStage.initStyle(StageStyle.TRANSPARENT);
            playerVsPlayerStage.initModality(Modality.APPLICATION_MODAL);
            playerVsPlayerStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png"))));
            playerVsPlayerStage.setResizable(false);

            playerVsPlayerStage.showAndWait();
            closeWindow();

        });

        view.getMultiplayerGame().setOnAction(event -> {
            model.setOnlineMultiplayer(true);
            model.setContinueInMultiplayer(true);
            model.setHost(true);
            closeWindow();
        });


    }

    private void closeWindow() {
        Stage stage = (Stage) view.getScene().getWindow();
        stage.close();
    }
}
