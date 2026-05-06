package game.integration_project1_zaroc.view.pages.gamesetupview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.model.selectionslider.PawnColorPickerModel;
import game.integration_project1_zaroc.model.selectionslider.StartingPlayerSelector;
import game.integration_project1_zaroc.utils.Observer;
import game.integration_project1_zaroc.view.sharedlogic.NavigationService;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.pawncolors.PawnColorPaths;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.util.Duration;
import java.util.Arrays;
import java.util.List;


public class GameSetupPresenter implements Observer {
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
        view.getResourceManager().addObserver(this);
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
            NavigationService.navigateToGameBoard(view.getResourceManager(),this.model);
        });


        view.getLeaderBoardButton().setOnAction(actionEvent -> {
            NavigationService.navigateToLeaderboard(view.getResourceManager(),this.model).showAndWait();
        });
        view.getInfoButton().setOnAction(event -> {
            NavigationService.navigateToRules(view.getResourceManager(),this.model).showAndWait();
        });
        view.getSettingsButton().setOnAction(actionEvent -> {
            NavigationService.navigateToSettings(view.getResourceManager(),this.model).showAndWait();
        });

        view.getReturnButton().setOnAction(event -> {
            model.setPlayer2(null);
            NavigationService.navigateToGameModeSelection(view.getResourceManager(),this.model);
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

    @Override
    public void updateLayout(Object args) {
        view.layoutNodes();
    }
}
