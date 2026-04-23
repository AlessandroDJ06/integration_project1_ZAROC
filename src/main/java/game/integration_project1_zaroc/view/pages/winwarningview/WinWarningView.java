package game.integration_project1_zaroc.view.pages.winwarningview;

import game.integration_project1_zaroc.view.components.buttons.TextButton;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class WinWarningView extends BorderPane {
    private ResourceManager resourceManager;
    private Label matchPoint;
    private Label winWarning;
    private Button closeButton;

    public WinWarningView(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        closeButton = new TextButton(resourceManager, "X");
        winWarning = new Label("TEST IS CLOSE TO WINNING!");
        matchPoint = new Label("MATCH POINT!");
    }

    private void layoutNodes() {
        this.winWarning.setFont(resourceManager.getFont(Fonts.PRESSSTART2PMEDIUM));
        this.matchPoint.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));

        Image boardBackgroundImage = resourceManager.getImage(Components.PEGVIEW);
        BackgroundSize backgroundSize = new BackgroundSize(400, 100, false, false, false, false);
        BackgroundImage backgroundImage = new BackgroundImage(
                boardBackgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );
        Background background = new Background(backgroundImage);
        setBackground(background);

        closeButton.setMaxHeight(30);
        closeButton.setMinHeight(30);
        closeButton.setPrefHeight(30);

        VBox vBox = new VBox(matchPoint, winWarning);
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);

        this.setPrefSize(400, 100);
        this.setMaxSize(400, 100);

        this.setTop(closeButton);
        BorderPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        this.setPadding(new Insets(10));
        this.setCenter(vBox);
    }

    Button getCloseButton() {
        return closeButton;
    }

    public Label getWinWarning() {
        return winWarning;
    }
}
