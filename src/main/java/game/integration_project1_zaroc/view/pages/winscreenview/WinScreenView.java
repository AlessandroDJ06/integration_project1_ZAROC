package game.integration_project1_zaroc.view.pages.winscreenview;

import game.integration_project1_zaroc.view.components.buttons.ShortButtonComponent;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class WinScreenView extends BorderPane {
    private ResourceManager resourceManager;
    private Label playerWon;
    private Label congratsLabel;
    private Text gameStats;
    private Text playerStats;
    private ShortButtonComponent returnButton;
    private ShortButtonComponent rematchButton;
    private Label gameStatsTitle;
    private Label playerStatsTitle;

    public WinScreenView(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        playerWon = new Label("");
        congratsLabel = new Label("Congratulations!");
        gameStats = new Text("");
        playerStats = new Text("");
        returnButton = new ShortButtonComponent(resourceManager, "Return to menu");
        rematchButton = new ShortButtonComponent(resourceManager, "Rematch");
        gameStatsTitle = new Label("Game stats");
        playerStatsTitle = new Label("Player stats");
    }

    void layoutNodes() {
        this.getChildren().clear();
        VBox gameStatsVBox = new VBox(gameStatsTitle, gameStats);
        gameStatsVBox.setAlignment(Pos.TOP_RIGHT);
        gameStatsVBox.setSpacing(10);
        gameStatsVBox.setMinWidth(200);

        VBox playerStatsVBox = new VBox(playerStatsTitle, playerStats);
        playerStatsVBox.setAlignment(Pos.TOP_LEFT);
        playerStatsVBox.setSpacing(10);
        playerStatsVBox.setMinWidth(200);

        Rectangle divider = new Rectangle(2, 150, Color.BLACK);

        HBox statsHBox = new HBox(gameStatsVBox, divider, playerStatsVBox);
        statsHBox.setSpacing(15);
        statsHBox.setAlignment(Pos.CENTER);

        //nieuwe medium font toegevoegd in Fonts enum
        gameStatsTitle.setFont(resourceManager.getFont(Fonts.PRESSSTART2PMEDIUM));
        playerStatsTitle.setFont(resourceManager.getFont(Fonts.PRESSSTART2PMEDIUM));
        gameStats.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));
        playerStats.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));

        gameStats.setTextAlignment(TextAlignment.LEFT);
        playerStats.setTextAlignment(TextAlignment.LEFT);
        gameStats.setLineSpacing(15);
        playerStats.setLineSpacing(15);

        playerWon.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        congratsLabel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));

        VBox textVBox = new VBox(playerWon, congratsLabel, statsHBox);
        textVBox.setSpacing(30);
        textVBox.setAlignment(Pos.CENTER);

        HBox buttonsHBox = new HBox(returnButton, rematchButton);
        returnButton.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));
        rematchButton.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));
        buttonsHBox.setSpacing(20);
        buttonsHBox.setAlignment(Pos.CENTER);

        VBox wholeVbox = new VBox(textVBox, buttonsHBox);
        wholeVbox.setAlignment(Pos.TOP_CENTER);
        wholeVbox.setSpacing(50);
        wholeVbox.setPadding(new Insets(40, 0, 40, 0));

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

        this.setPrefSize(450, 450);
        this.setMinSize(450, 450);
        this.setMaxSize(450, 450);
        this.setCenter(wholeVbox);
    }

    ResourceManager getResourceManager() {
        return resourceManager;
    }

    Label getPlayerWon() {
        return playerWon;
    }

    Button getRematchButton() {
        return rematchButton;
    }

    Button getReturnButton() {
        return returnButton;
    }

    Text getGameStats() {
        return gameStats;
    }

    Text getPlayerStats() {
        return playerStats;
    }
}