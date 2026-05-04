package game.integration_project1_zaroc.view.pages.pausescreenview;

import game.integration_project1_zaroc.view.components.buttons.LongButtonComponent;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class PauseScreenView extends BorderPane {
    private ResourceManager resourceManager;
    private Label gamePaused;
    private LongButtonComponent continueButton;
    private Label noteUnfinishedGame;
    private LongButtonComponent returnButton;

    public PauseScreenView(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        intialiseNodes();
        layoutNodes();
    }

    private void intialiseNodes() {
        gamePaused = new Label("GAME PAUSED");
        continueButton = new LongButtonComponent(resourceManager, "Continue game");
        noteUnfinishedGame = new Label("NOTE: You are playing as a guest! Current game will be DELETED permanently!");
        returnButton = new LongButtonComponent(resourceManager, "Return to menu");
    }

    void layoutNodes() {
        continueButton.updateLayout();
        returnButton.updateLayout();
        gamePaused.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        gamePaused.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
        noteUnfinishedGame.setFont(resourceManager.getFont(Fonts.PRESSSTART2PMEDIUM));
        noteUnfinishedGame.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        noteUnfinishedGame.setWrapText(true);
        noteUnfinishedGame.setMaxWidth(250);

        VBox wholeVBox = new VBox(gamePaused, continueButton, returnButton,noteUnfinishedGame);
        wholeVBox.setAlignment(Pos.CENTER);
        wholeVBox.setSpacing(15);

        Image boardBackgroundImage = resourceManager.getImage(Components.PEGVIEW);
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(
                boardBackgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );
        setBackground(new Background(backgroundImage));

        this.setPrefSize(400, 400);
        this.setMinSize(400, 400);
        this.setMaxSize(400, 400);
        this.setCenter(wholeVBox);
    }

    public LongButtonComponent getContinueButton() {
        return continueButton;
    }

    LongButtonComponent getReturnButton() {
        return returnButton;
    }
    ResourceManager getResourceManager(){
        return resourceManager;
    }

    public Label getNoteUnfinishedGame() {
        return noteUnfinishedGame;
    }
}