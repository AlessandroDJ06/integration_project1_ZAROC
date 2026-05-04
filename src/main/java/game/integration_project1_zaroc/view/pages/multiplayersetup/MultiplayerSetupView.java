package game.integration_project1_zaroc.view.pages.multiplayersetup;

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

public class MultiplayerSetupView extends BorderPane {
    private ResourceManager resourceManager;
    private TextButton returnButton;
    private LongButtonComponent hostGame;
    private LongButtonComponent joinGame;


    public MultiplayerSetupView(ResourceManager resourceManager){
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();

    }

    private void initialiseNodes(){
        this.returnButton = new TextButton(resourceManager,"←");
        this.hostGame = new LongButtonComponent(resourceManager,"Play host");
        this.joinGame= new LongButtonComponent(resourceManager,"Join as guest");
    }

    void layoutNodes(){
        returnButton.updateLayout();
        hostGame.updateLayout();
        joinGame.updateLayout();
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

        Label hostGameLabel = new Label("Play as Host");
        hostGameLabel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        hostGameLabel.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        Label spacer = new Label("-- or --");
        spacer.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        spacer.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        Label joinGameLabel = new Label("Play as Guest");
        joinGameLabel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        joinGameLabel.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));


        centerContainer.getChildren().addAll(hostGameLabel,hostGame,spacer,joinGameLabel,joinGame);
        setCenter(centerContainer);
        centerContainer.setMaxWidth(240);

    }

    ResourceManager getResourceManager() {
        return resourceManager;
    }

    Button getJoinGame() {
        return joinGame;
    }

    Button getHostGame() {
        return hostGame;
    }

    Button getReturnButton() {
        return returnButton;
    }
}
