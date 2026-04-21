package game.integration_project1_zaroc.view.pages.selectgamemodeview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.view.pages.boardview.GameBoardPresenter;
import game.integration_project1_zaroc.view.pages.boardview.GameBoardView;
import game.integration_project1_zaroc.view.pages.gamesetupview.GameSetupPresenter;
import game.integration_project1_zaroc.view.pages.gamesetupview.GameSetupView;
import game.integration_project1_zaroc.view.pages.multiplayerguestview.MultiPlayerGuestPresenter;
import game.integration_project1_zaroc.view.pages.multiplayerguestview.MultiPlayerGuestView;
import game.integration_project1_zaroc.view.pages.multiplayerhostview.MultiPlayerHostPresenter;
import game.integration_project1_zaroc.view.pages.multiplayerhostview.MultiPlayerHostView;
import game.integration_project1_zaroc.view.pages.multiplayersetup.MultiplayerSetupPresenter;
import game.integration_project1_zaroc.view.pages.multiplayersetup.MultiplayerSetupView;
import game.integration_project1_zaroc.view.pages.playervsaiview.PlayerVsAiPresenter;
import game.integration_project1_zaroc.view.pages.playervsaiview.PlayerVsAiView;
import game.integration_project1_zaroc.view.pages.playervsplayerview.PlayerVsPlayerPresenter;
import game.integration_project1_zaroc.view.pages.playervsplayerview.PlayerVsPlayerView;
import game.integration_project1_zaroc.view.pages.ruleview.RuleView;
import game.integration_project1_zaroc.view.pages.ruleview.RuleViewPresenter;
import game.integration_project1_zaroc.view.pages.settingsview.SettingsPresenter;
import game.integration_project1_zaroc.view.pages.settingsview.SettingsView;
import game.integration_project1_zaroc.view.pages.statisticsview.StatisticsPresenter;
import game.integration_project1_zaroc.view.pages.statisticsview.StatisticsView;
import game.integration_project1_zaroc.view.pages.unfinishedgamesview.UnfinishedGamesPresenter;
import game.integration_project1_zaroc.view.pages.unfinishedgamesview.UnfinishedGamesView;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class SelectGamemodePresenter {
    private SelectGamemodeView view;
    private AppController model;
    private List<Button> buttons;

    public SelectGamemodePresenter(AppController model , SelectGamemodeView view){
        this.view = view;
        this.model = model;
        addEventHandlers();
    }

    private void addEventHandlers(){
        for (Button button : Arrays.asList(
                view.getInfoButton(),
                view.getSettingsButton(),
                view.getProfileButton(),
                view.getPlayerVsAiButton(),
                view.getPlayerVsPlayerButton(),
                view.getUnfinishedGamesButton(),
                view.getMultiPlayerButton()
        )){
            GeneralEventhandlers.addHoverEffect(button);
            GeneralEventhandlers.addSoundEffect(button, view.getResourceManager());
        }

        view.getSettingsButton().setOnAction(actionEvent -> {
            SettingsView settingsView = new SettingsView(view.getResourceManager());
            new SettingsPresenter(settingsView,this.model);
            Scene settingsScene = new Scene(settingsView);
            settingsScene.setFill(Color.TRANSPARENT);
            Stage settingsStage = new Stage();
            settingsStage.setScene(settingsScene);
            settingsStage.setTitle("Settings");
            settingsStage.initStyle(StageStyle.TRANSPARENT);
            settingsStage.initModality(Modality.APPLICATION_MODAL);
            settingsStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png"))));
            settingsStage.setResizable(false);
            settingsStage.showAndWait();

        });

        view.getInfoButton().setOnAction(event -> {

            RuleView ruleView = new RuleView(view.getResourceManager());
            new RuleViewPresenter(ruleView,this.model);
            Scene ruleScene = new Scene(ruleView);
            ruleScene.setFill(Color.TRANSPARENT);
            Stage ruleStage = new Stage();
            ruleStage.setScene(ruleScene);
            ruleStage.setTitle("Regels");
            ruleStage.initStyle(StageStyle.TRANSPARENT);
            ruleStage.initModality(Modality.APPLICATION_MODAL);
            ruleStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png"))));
            ruleStage.setResizable(false);
            ruleStage.showAndWait();


        });

        view.getPlayerVsAiButton().setOnAction(event -> {
            PlayerVsAiView playerVsAiView = new PlayerVsAiView(view.getResourceManager());
            new PlayerVsAiPresenter(playerVsAiView,model);
            Scene playerVsAiScene = new Scene(playerVsAiView,900,750);
            playerVsAiScene.setFill(Color.TRANSPARENT);
            Stage playerVsAiStage = new Stage();
            playerVsAiStage.setScene(playerVsAiScene);
            playerVsAiStage.setTitle("Selecteer tegenstander");
            playerVsAiStage.initStyle(StageStyle.TRANSPARENT);
            playerVsAiStage.initModality(Modality.APPLICATION_MODAL);
            playerVsAiStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png"))));
            playerVsAiStage.setResizable(false);
            playerVsAiStage.showAndWait();
            if (model.getPlayer2() != null){
                navigateToGameSetup();
            }
        });

        view.getPlayerVsPlayerButton().setOnAction(event -> {
            PlayerVsPlayerView playerVsPlayerView = new PlayerVsPlayerView(view.getResourceManager());
            new PlayerVsPlayerPresenter(playerVsPlayerView, model);

            Scene playerVsPlayerScene = new Scene(playerVsPlayerView, 900, 750);
            playerVsPlayerScene.setFill(Color.TRANSPARENT);

            Stage playerVsPlayerStage = new Stage();
            playerVsPlayerStage.setScene(playerVsPlayerScene);
            playerVsPlayerStage.setTitle("Speler vs Speler"); // Titel iets aangepast voor de context
            playerVsPlayerStage.initStyle(StageStyle.TRANSPARENT);
            playerVsPlayerStage.initModality(Modality.APPLICATION_MODAL);
            playerVsPlayerStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png"))));
            playerVsPlayerStage.setResizable(false);

            playerVsPlayerStage.showAndWait();

            if (model.getPlayer2() != null){
                navigateToGameSetup();
            }
        });

        view.getUnfinishedGamesButton().setOnAction(event -> {
            UnfinishedGamesView unfinishedView = new UnfinishedGamesView(view.getResourceManager());
            new UnfinishedGamesPresenter(unfinishedView, model);
            Scene unfinishedScene = new Scene(unfinishedView, 900, 750);
            unfinishedScene.setFill(Color.TRANSPARENT);
            Stage unfinishedStage = new Stage();
            unfinishedStage.setScene(unfinishedScene);
            unfinishedStage.setTitle("Hervat een spel");
            unfinishedStage.initStyle(StageStyle.TRANSPARENT);
            unfinishedStage.initModality(Modality.APPLICATION_MODAL);
            unfinishedStage.getIcons().add(new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png")
            )));
            unfinishedStage.setResizable(false);
            unfinishedStage.showAndWait();
            checkIfGameIsEmpty();

        });

        view.getMultiPlayerButton().setOnAction(event -> {
            MultiplayerSetupView multiplayerSetupView = new MultiplayerSetupView(view.getResourceManager());
            new MultiplayerSetupPresenter(multiplayerSetupView, model);

            Scene multiplayerScene = new Scene(multiplayerSetupView);
            multiplayerScene.setFill(Color.TRANSPARENT);

            Stage multiplayerStage = new Stage();
            multiplayerStage.setScene(multiplayerScene);
            multiplayerStage.setTitle("Multiplayer Setup");
            multiplayerStage.initStyle(StageStyle.TRANSPARENT);
            multiplayerStage.initModality(Modality.APPLICATION_MODAL);

            multiplayerStage.getIcons().add(new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png")
            )));

            multiplayerStage.setResizable(false);

            multiplayerStage.showAndWait();

            if (model.isOnlineMultiplayer()){
                if (model.isHost()){
                    navigateHostView();
                    checkIfGameIsEmpty();
                } else {
                    navigateGuestView();
                    checkIfGameIsEmpty();
                }
            }
        });

        view.getProfileButton().setOnAction(event -> {

            StatisticsView statisticsView = new StatisticsView(view.getResourceManager());
            new StatisticsPresenter(statisticsView,model);
            Scene statsScene = new Scene(statisticsView);
            statsScene.setFill(Color.TRANSPARENT);
            Stage statsStage = new Stage();

            statsStage.setScene(statsScene);
            statsStage.initStyle(StageStyle.TRANSPARENT);
            statsStage.initModality(Modality.APPLICATION_MODAL);
            statsStage.setResizable(false);
            statsStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png"))));
            statsStage.showAndWait();
        });

    }

    private void checkIfGameIsEmpty() {
        if (model.getGame() != null){
            navigateToGame();
        }
    }

    private void navigateToGameSetup() {
        GameSetupView gameSetupView = new GameSetupView(view.getResourceManager());
        new GameSetupPresenter(gameSetupView,model);
        view.getScene().setRoot(gameSetupView);
    }

    private void navigateToGame(){
        GameBoardView gameBoardView = new GameBoardView(view.getResourceManager());
        new GameBoardPresenter(gameBoardView,model);
        view.getScene().setRoot(gameBoardView);
    }


    private void navigateHostView(){
        MultiPlayerHostView multiPlayerHostView = new MultiPlayerHostView(view.getResourceManager());
        new MultiPlayerHostPresenter(multiPlayerHostView, model);
        Scene hostScene = new Scene(multiPlayerHostView, 900, 750);
        hostScene.setFill(Color.TRANSPARENT);
        Stage hostStage = new Stage();
        hostStage.setScene(hostScene);
        hostStage.setTitle("Host Game");
        hostStage.initStyle(StageStyle.TRANSPARENT);
        hostStage.initModality(Modality.APPLICATION_MODAL);
        hostStage.getIcons().add(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png")
        )));

        hostStage.setResizable(false);
        hostStage.showAndWait();
    }

    private void navigateGuestView(){
        MultiPlayerGuestView multiPlayerGuestView = new MultiPlayerGuestView(view.getResourceManager());
        new MultiPlayerGuestPresenter(multiPlayerGuestView, model);
        Scene guestScene = new Scene(multiPlayerGuestView, 900, 750);
        guestScene.setFill(Color.TRANSPARENT);
        Stage guestStage = new Stage();
        guestStage.setScene(guestScene);
        guestStage.setTitle("Join Game");
        guestStage.initStyle(StageStyle.TRANSPARENT);
        guestStage.initModality(Modality.APPLICATION_MODAL);
        guestStage.getIcons().add(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png")
        )));

        guestStage.setResizable(false);
        guestStage.showAndWait();
    }
}
