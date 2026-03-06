package game.integration_project1_zaroc.view.boardview;
import game.integration_project1_zaroc.view.components.BoardComponent;
import game.integration_project1_zaroc.view.components.GeneralActionsComponent;
import game.integration_project1_zaroc.view.components.PlayersPlayingComponent;
import game.integration_project1_zaroc.view.core.ResourceManager;
import game.integration_project1_zaroc.view.core.profilePictures.ProfilePictures;
import game.integration_project1_zaroc.view.core.themes.Components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class GameBoardView extends BorderPane {

    private BoardComponent board;
    private Button undoButton;
    private Button settingsButton;
    private Button infoButton;
    private ResourceManager resourceManager;
    private PlayersPlayingComponent playersPlayingComponent;


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
    }

    public void layoutNodes() {
        playersPlayingComponent.setPlayerOnePfp(ProfilePictures.JAMES);
        playersPlayingComponent.setPlayerTwoPfp(ProfilePictures.BADBUNNY);

        playersPlayingComponent.setFirstPlayer("Alessandro");
        playersPlayingComponent.setSecondPlayer("JEFFRY");

        BorderPane pegView = new BorderPane();
        Rectangle rect = new Rectangle();
        Image boardBackgroundImage = resourceManager.getImage(Components.PEGVIEW);
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(
                boardBackgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );
        pegView.setBackground(new Background(backgroundImage));
        pegView.setMaxSize(400,400);
        pegView.setPrefSize(400,400);

        List<ImageView> poles = new ArrayList<>();
        for (int i = 0 ; i < 18 ;i++){
            poles.add(new ImageView(resourceManager.getImage(Components.POLE)));
        }

        HBox pegRow4 = new HBox(poles.get(0),poles.get(1),poles.get(2),poles.get(3));
        pegRow4.setAlignment(Pos.CENTER);
        pegRow4.setSpacing(50);
        HBox pegRow3 = new HBox(poles.get(4),poles.get(5),poles.get(6),poles.get(7));
        pegRow3.setAlignment(Pos.CENTER);
        pegRow3.setSpacing(50);
        HBox pegRow2 = new HBox(poles.get(8),poles.get(9),poles.get(10),poles.get(11),poles.get(12));
        pegRow2.setAlignment(Pos.CENTER);
        pegRow2.setSpacing(50);

        VBox rows = new VBox(pegRow4,pegRow3,pegRow2);
        rows.setSpacing(15);
        pegView.setCenter(rows);
        rows.setAlignment(Pos.CENTER);


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
        VBox undoButtonVbox = new VBox(this.undoButton);
        setLeft(undoButtonVbox);
        BorderPane.setAlignment(undoButtonVbox,Pos.TOP_LEFT);
        undoButtonVbox.setPadding(new Insets(30,0,0,30));

        this.setStyle("-fx-background-color: " + this.resourceManager.getTheme().getColor() + ";");
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
}