package game.integration_project1_zaroc.view.pages.boardview;
import game.integration_project1_zaroc.view.components.BoardComponent;
import game.integration_project1_zaroc.view.components.buttons.GeneralActionsComponent;
import game.integration_project1_zaroc.view.components.PegSideViewComponent;
import game.integration_project1_zaroc.view.components.PlayersPlayingComponent;
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
    }

    public void layoutNodes() {
        playersPlayingComponent.setPlayerOnePfp(ProfilePictures.JAMES);
        playersPlayingComponent.setPlayerTwoPfp(ProfilePictures.BADBUNNY);

        Region spacer = new Region();
        HBox boardWithPegView = new HBox(board,spacer,pegView);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        boardWithPegView.setAlignment(Pos.CENTER);
        VBox boardView = new VBox();

        boardView.getChildren().addAll(playersPlayingComponent,boardWithPegView);
        boardView.setSpacing(50);
        boardView.setPadding(new Insets(40));
        setCenter(boardView);
        boardView.setMaxWidth(1200);
        boardView.setAlignment(Pos.TOP_CENTER);
        BorderPane.setAlignment(boardView,Pos.CENTER);

        VBox infoAndSettingsVbox = new VBox(this.settingsButton,this.infoButton);
        setRight(infoAndSettingsVbox);
        BorderPane.setAlignment(infoAndSettingsVbox,Pos.TOP_RIGHT);
        infoAndSettingsVbox.setPadding(new Insets(30,30,0,0));
        infoAndSettingsVbox.setSpacing(15);
        HBox undoHbox = new HBox(this.undoButton,undoTimer);

        undoHbox.setAlignment(Pos.CENTER_LEFT);
        undoHbox.setSpacing(30);

        VBox undoVbox = new VBox(undoHbox);
        undoVbox.setPadding(new Insets(30, 0, 0, 30));
        setLeft(undoVbox);

        this.setStyle("-fx-background-color: " + this.resourceManager.getTheme().getColor() + ";");

        this.undoTimer.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
        this.undoTimer.setStyle("-fx-font-family: '" + resourceManager.getFont(Fonts.PRESSSTART2PSMALL).getFamily()
                + "'; " + "-fx-font-size: 24px;");
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
}