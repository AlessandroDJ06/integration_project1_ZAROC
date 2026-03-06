package game.integration_project1_zaroc.view;
import game.integration_project1_zaroc.components.BoardComponent;
import game.integration_project1_zaroc.components.GeneralActionsComponent;
import game.integration_project1_zaroc.components.PlayersPlayingComponent;
import game.integration_project1_zaroc.core.ResourceManager;
import game.integration_project1_zaroc.core.profilePictures.ProfilePictures;
import game.integration_project1_zaroc.core.themes.Components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

public class GameBoardView extends BorderPane {

    private BoardComponent board;
    private Button undoButton;
    private Button settingsButton;
    private Button infoButton;
    private ResourceManager resourceManager;


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
    }

    public void layoutNodes() {
        PlayersPlayingComponent playersPlaying = new PlayersPlayingComponent("Alessandro","Alessandro",resourceManager);
        playersPlaying.setPlayerOnePfp(ProfilePictures.JAMES);
        playersPlaying.setPlayerTwoPfp(ProfilePictures.BADBUNNY);
        VBox boardView = new VBox();
        boardView.getChildren().addAll(playersPlaying,board);
        boardView.setSpacing(50);
        boardView.setPadding(new Insets(40));
        setCenter(boardView);
        boardView.setMaxWidth(600);
        BorderPane.setAlignment(boardView,Pos.CENTER);

        VBox infoAndSettingsVbox = new VBox(this.settingsButton,this.infoButton);
        setRight(infoAndSettingsVbox);
        BorderPane.setAlignment(infoAndSettingsVbox,Pos.TOP_RIGHT);
        infoAndSettingsVbox.setPadding(new Insets(30,30,0,0));
        infoAndSettingsVbox.setSpacing(15);
        VBox undoButtonVbox = new VBox(this.undoButton);
        setLeft(undoButtonVbox);
        BorderPane.setAlignment(undoButtonVbox,Pos.TOP_LEFT);
        undoButtonVbox.setPadding(new Insets(30,0,0,30));
        this.setStyle("-fx-background-color: " + this.resourceManager.getTheme().getColor() + ";");
    }
}