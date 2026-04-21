package game.integration_project1_zaroc.view.pages.boardview;
import game.integration_project1_zaroc.view.components.BoardComponent;
import game.integration_project1_zaroc.view.components.buttons.GeneralActionsComponent;
import game.integration_project1_zaroc.view.components.PegSideViewComponent;
import game.integration_project1_zaroc.view.components.PlayersPlayingComponent;
import game.integration_project1_zaroc.view.components.buttons.ShortButtonComponent;
import game.integration_project1_zaroc.view.components.buttons.TextButton;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.profilePictures.ProfilePictures;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;


public class GameBoardView extends BorderPane {

    private BoardComponent board;
    private Button undoButton;
    private Button settingsButton;
    private Button infoButton;
    private ResourceManager resourceManager;
    private PlayersPlayingComponent playersPlayingComponent;
    private PegSideViewComponent pegView;
    private Label undoTimer;
    private Button skipButton;



    public GameBoardView(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    public void initialiseNodes() {
        this.board = new BoardComponent(this.resourceManager);
        this.undoButton = new GeneralActionsComponent(this.resourceManager, Components.UNDO);
        this.settingsButton = new GeneralActionsComponent(this.resourceManager,Components.SETTINGS);
        this.infoButton = new GeneralActionsComponent(this.resourceManager,Components.RULES);
        this.playersPlayingComponent = new PlayersPlayingComponent(resourceManager);
        this.pegView = new PegSideViewComponent(this.resourceManager);
        this.undoTimer = new Label("");
        this.skipButton = new TextButton(resourceManager,"SKIP");
    }

    public void layoutNodes() {
        playersPlayingComponent.setPlayerOnePfp(ProfilePictures.JAMES);
        playersPlayingComponent.setPlayerTwoPfp(ProfilePictures.BADBUNNY);

        BorderPane topHeader = new BorderPane();
        topHeader.setPadding(new Insets(30, 30, 0, 30));


        skipButton.setFont(resourceManager.getFont(Fonts.PRESSSTART2PMEDIUM));
        skipButton.setVisible(false);
        skipButton.setDisable(true);
        VBox undoVBox = new VBox(this.undoTimer,this.skipButton);
        undoVBox.setAlignment(Pos.CENTER);
        undoVBox.setSpacing(10);

        HBox undoHbox = new HBox(this.undoButton, undoVBox);
        undoHbox.setAlignment(Pos.CENTER_LEFT);
        undoHbox.setSpacing(30);
        undoHbox.setMaxHeight(Region.USE_PREF_SIZE);

        topHeader.setLeft(undoHbox);
        BorderPane.setAlignment(undoHbox, Pos.TOP_LEFT);

        topHeader.setCenter(playersPlayingComponent);
        BorderPane.setAlignment(playersPlayingComponent, Pos.TOP_CENTER);

        VBox infoAndSettingsVbox = new VBox(this.settingsButton, this.infoButton);
        infoAndSettingsVbox.setSpacing(15);
        infoAndSettingsVbox.setMaxHeight(Region.USE_PREF_SIZE);

        topHeader.setRight(infoAndSettingsVbox);
        BorderPane.setAlignment(infoAndSettingsVbox, Pos.TOP_RIGHT);

        setTop(topHeader);

        Region spacer = new Region();
        spacer.setMinWidth(200);

        HBox boardWithPegView = new HBox(board, spacer, pegView);
        boardWithPegView.setAlignment(Pos.CENTER);

        VBox boardVbox = new VBox(boardWithPegView);
        boardVbox.setAlignment(Pos.CENTER);
        boardVbox.setPadding(new Insets(40));

        setCenter(boardVbox);

        this.setStyle("-fx-background-color: " + this.resourceManager.getTheme().getColor() + ";");

        this.undoTimer.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
        this.undoTimer.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
    }


    BoardComponent getBoard() {
        return board;
    }

    PlayersPlayingComponent getPlayersPlayingComponent() {
        return playersPlayingComponent;
    }

    ResourceManager getResourceManager() {
        return resourceManager;
    }

    Button getInfoButton() {
        return infoButton;
    }

    Button getSettingsButton() {
        return settingsButton;
    }

    Button getUndoButton() {
        return undoButton;
    }

    HBox getPegRowThree() {
        return pegView.getPegRowThree();
    }

    HBox getPegRowTwo() {
        return pegView.getPegRowTwo();
    }

    HBox getPegRowFour() {
        return pegView.getPegRowFour();
    }

    List<VBox> getPegContainers() {
        return pegView.getPegContainers();
    }
    Label getUndoTimer() {
        return undoTimer;
    }

    Button getSkipButton(){
        return skipButton;
    }
}