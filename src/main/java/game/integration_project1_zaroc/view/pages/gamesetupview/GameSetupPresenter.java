package game.integration_project1_zaroc.view.pages.gamesetupview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.model.selectionslider.PawnColorPickerModel;
import game.integration_project1_zaroc.model.selectionslider.StartingPlayerSelector;
import game.integration_project1_zaroc.view.pages.boardview.GameBoardPresenter;
import game.integration_project1_zaroc.view.pages.boardview.GameBoardView;
import game.integration_project1_zaroc.view.pages.leaderboardview.LeaderboardPresenter;
import game.integration_project1_zaroc.view.pages.leaderboardview.LeaderboardView;
import game.integration_project1_zaroc.view.pages.ruleview.RuleView;
import game.integration_project1_zaroc.view.pages.ruleview.RuleViewPresenter;
import game.integration_project1_zaroc.view.pages.selectgamemodeview.SelectGamemodePresenter;
import game.integration_project1_zaroc.view.pages.selectgamemodeview.SelectGamemodeView;
import game.integration_project1_zaroc.view.pages.settingsview.SettingsPresenter;
import game.integration_project1_zaroc.view.pages.settingsview.SettingsView;
import game.integration_project1_zaroc.view.pages.statisticsview.StatisticsPresenter;
import game.integration_project1_zaroc.view.pages.statisticsview.StatisticsView;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.pawncolors.PawnColorPaths;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GameSetupPresenter {
    private AppController model;
    private GameSetupView view;
    private List<Button> buttons;
    private String[] players;

    private PawnColorPickerModel colorOne;
    private PawnColorPickerModel colorTwo;
    private StartingPlayerSelector startingPlayerSelector;

    public GameSetupPresenter(GameSetupView view , AppController appController){
        this.view = view;
        this.model = appController;
        this.buttons = Arrays.asList(
                view.getProfileButton(),
                view.getSettingsButton(),
                view.getInfoButton(),
                view.getColorPickerOne().getLeftButton(),
                view.getColorPickerOne().getRightButton(),
                view.getColorPickerTwo().getLeftButton(),
                view.getColorPickerTwo().getRightButton(),
                view.getLeaderBoardButton(),
                view.getReturnButton()
        );

        this.colorOne = appController.getColorOne();
        this.colorTwo = appController.getColorTwo();
        this.startingPlayerSelector = appController.getStartingPlayerSelector();
        this.players = new String[] {"player1","player2"};

        addEventHandlers();
        updateView();
        addAnimations();
    }

    private void addEventHandlers(){


        for (Button button : buttons){
            GeneralEventhandlers.addHoverEffect(button);
            GeneralEventhandlers.addSoundEffect(button, view.getResourceManager());
        }

        view.getColorPickerOne().getLeftButton().setOnAction(event -> {
            colorOne.decreaseCurrentIndex();
            colorTwo.setCurrentIndexOtherPicker(colorOne.getCurrentIndex());
            model.setPlayer1Color();
            updateView();
        });

        view.getColorPickerTwo().getLeftButton().setOnAction(event -> {
            colorTwo.decreaseCurrentIndex();
            colorOne.setCurrentIndexOtherPicker(colorTwo.getCurrentIndex());
            model.setPlayer2Color();
            updateView();
        });

        view.getColorPickerOne().getRightButton().setOnAction(event -> {
            colorOne.increaseCurrentIndex();
            colorTwo.setCurrentIndexOtherPicker(colorOne.getCurrentIndex());
            model.setPlayer1Color();
            updateView();
        });

        view.getColorPickerTwo().getRightButton().setOnAction(event -> {
            colorTwo.increaseCurrentIndex();
            colorOne.setCurrentIndexOtherPicker(colorTwo.getCurrentIndex());
            model.setPlayer2Color();
            updateView();
        });

        view.getStartingPlayerPicker().getRightButton().setOnAction(event -> {
            startingPlayerSelector.increaseCurrentIndex();
            updateView();
        });
        view.getStartingPlayerPicker().getLeftButton().setOnAction(event -> {
            startingPlayerSelector.decreaseCurrentIndex();
            updateView();
        });

        view.getCreateGameButton().setOnAction(event -> {
            model.createGame();
            GameBoardView GameBoardView = new GameBoardView(this.view.getResourceManager());
            new GameBoardPresenter(GameBoardView,model);
            view.getScene().setRoot(GameBoardView);
        });


        view.getLeaderBoardButton().setOnAction(actionEvent -> {
            LeaderboardView leaderboardView = new LeaderboardView(view.getResourceManager());
            new LeaderboardPresenter(leaderboardView,this.model);
            Scene leaderboardScene = new Scene(leaderboardView);
            leaderboardScene.setFill(Color.TRANSPARENT);
            Stage leaderboardStage = new Stage();
            leaderboardStage.setScene(leaderboardScene);
            leaderboardStage.setTitle("Leaderboard");
            leaderboardStage.initStyle(StageStyle.TRANSPARENT);
            leaderboardStage.initModality(Modality.APPLICATION_MODAL);
            leaderboardStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png"))));
            leaderboardStage.setResizable(false);
            leaderboardStage.showAndWait();

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

        view.getReturnButton().setOnAction(event -> {
            model.setPlayer2(null);
            SelectGamemodeView gamemodeView = new SelectGamemodeView(view.getResourceManager());
            new SelectGamemodePresenter(model,gamemodeView);
            view.getScene().setRoot(gamemodeView);
        });

    }

    private void updateView(){
        view.getColorPickerOne().getImageView().setImage(
                view.getResourceManager().getPawnColor(PawnColorPaths.values()[colorOne.getCurrentIndex()])
        );
        view.getColorPickerTwo().getImageView().setImage(
                view.getResourceManager().getPawnColor(PawnColorPaths.values()[colorTwo.getCurrentIndex()])
        );

        view.getStartingPlayerPicker().getLabel().setText(players[startingPlayerSelector.getCurrentIndex()]);
    }

    private void addAnimations() {
        Button startBtn = view.getCreateGameButton();
        Timeline pulse = new Timeline();
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.setAutoReverse(true);

        KeyValue kvX = new KeyValue(startBtn.scaleXProperty(), 1.1);
        KeyValue kvY = new KeyValue(startBtn.scaleYProperty(), 1.1);

        KeyFrame kf = new KeyFrame(Duration.millis(800), kvX, kvY);

        pulse.getKeyFrames().add(kf);
        pulse.play();
    }
}
