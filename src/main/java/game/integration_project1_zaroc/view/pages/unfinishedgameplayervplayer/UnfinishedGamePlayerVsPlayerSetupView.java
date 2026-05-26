package game.integration_project1_zaroc.view.pages.unfinishedgameplayervplayer;

import game.integration_project1_zaroc.view.components.buttons.LongButtonComponent;
import game.integration_project1_zaroc.view.components.buttons.TextButton;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.utils.LayoutHelpers;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class UnfinishedGamePlayerVsPlayerSetupView extends BorderPane {
    private ResourceManager resourceManager;
    private Button returnButton;
    private Button localGame;
    private Button multiplayerGame;


    public UnfinishedGamePlayerVsPlayerSetupView(ResourceManager resourceManager){
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();

    }

    private void initialiseNodes(){
        this.returnButton = new TextButton(resourceManager,"←");
        this.localGame = new LongButtonComponent(resourceManager,"Play local");
        this.multiplayerGame = new LongButtonComponent(resourceManager,"Play online");
    }

    void layoutNodes(){
        this.getChildren().clear();
        setBackground(new Background(LayoutHelpers.setBackground(this.resourceManager, 550, 650)));
        this.setPrefSize(550, 650);
        this.setMaxSize(550, 650);
        this.setPadding(new Insets(60, 40, 40, 40));

        // === Top ===
        returnButton.setMaxSize(40, 40);
        HBox titelSection = new HBox();
        titelSection.setAlignment(Pos.CENTER);
        titelSection.setPadding(new Insets(15));

        Label title = new Label("SETUP");
        title.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        title.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        Region leftFiller = new Region();
        Region rightFiller = new Region();
        Region rightSpacer = new Region();
        rightSpacer.prefWidthProperty().bind(returnButton.widthProperty());
        HBox.setHgrow(leftFiller, Priority.ALWAYS);
        HBox.setHgrow(rightFiller, Priority.ALWAYS);

        titelSection.getChildren().addAll(returnButton, leftFiller, title, rightFiller, rightSpacer);
        setTop(titelSection);

        // === Center ===
        VBox centerContainer = new VBox(30);
        centerContainer.setAlignment(Pos.CENTER);

        Label hostGameLabel = new Label("Local");
        hostGameLabel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        hostGameLabel.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        Label spacer = new Label("-- or --");
        spacer.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        spacer.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        Label joinGameLabel = new Label("Multiplayer");
        joinGameLabel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        joinGameLabel.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));


        centerContainer.getChildren().addAll(hostGameLabel, localGame,spacer,joinGameLabel, multiplayerGame);
        setCenter(centerContainer);
        centerContainer.setMaxWidth(240);

    }

    ResourceManager getResourceManager() {
        return resourceManager;
    }

    Button getMultiplayerGame() {
        return multiplayerGame;
    }

    Button getLocalGame() {
        return localGame;
    }

    Button getReturnButton() {
        return returnButton;
    }
}
