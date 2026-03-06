package game.integration_project1_zaroc.view.boardview;
import game.integration_project1_zaroc.components.BoardComponent;
import game.integration_project1_zaroc.components.GeneralActionsComponent;
import game.integration_project1_zaroc.components.PlayersPlayingComponent;
import game.integration_project1_zaroc.core.ResourceManager;
import game.integration_project1_zaroc.core.profilePictures.ProfilePictures;
import game.integration_project1_zaroc.core.themes.Components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.w3c.dom.css.Rect;

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

        Region spacer = new Region();
        HBox boardWithPegView = new HBox(board,spacer,pegView);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        boardWithPegView.setAlignment(Pos.CENTER);
        VBox boardView = new VBox();

        boardView.getChildren().addAll(playersPlaying,boardWithPegView);
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
}