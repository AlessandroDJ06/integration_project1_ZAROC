package game.integration_project1_zaroc.view.pages.playervsplayerview;

import game.integration_project1_zaroc.view.components.buttons.LongButtonComponent;
import game.integration_project1_zaroc.view.components.buttons.TextButton;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class PlayerVsPlayerView extends BorderPane {
    private Button returnButton;
    private ResourceManager resourceManager;

    private ImageView playerOnePfp;
    private Label playerOneName;

    private ImageView playerTwoPfp;
    private Label playerTwoName;

    private Button loginPlayerTwo;
    private Button createAccountPlayerTwo;
    private Button startGame;

    private VBox loginButtons;
    private VBox playerTwoInfo;
    private VBox playerOneInfo;

    private HBox content;

    public PlayerVsPlayerView(ResourceManager resourceManager){
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes(){
        this.returnButton = new TextButton(resourceManager,"←");
        this.playerOnePfp = new ImageView();
        this.playerOneName = new Label();

        this.playerTwoPfp = new ImageView();
        this.playerTwoName = new Label();

        this.createAccountPlayerTwo = new LongButtonComponent(resourceManager,"CREATE ACCOUNT");
        this.loginPlayerTwo = new LongButtonComponent(resourceManager, "LOGIN");
        this.startGame = new LongButtonComponent(resourceManager,"START");

        this.loginButtons = new VBox();
        this.playerTwoInfo = new VBox();
        this.playerOneInfo = new VBox();

        this.content = new HBox();
    }

    private void layoutNodes(){
        BorderPane centralContainer = new BorderPane();
        centralContainer.setBackground(new Background(new BackgroundImage(
                resourceManager.getImage(Components.LARGECONTAINER),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, true, false)
        )));
        centralContainer.setMaxSize(800, 640);
        centralContainer.setPrefSize(800, 640);

        Label title = new Label("PLAYER VS PLAYER");
        title.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        title.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        HBox titelSection = new HBox(returnButton, new Region(), title, new Region(), createSpacer(returnButton));
        HBox.setHgrow(titelSection.getChildren().get(1), Priority.ALWAYS);
        HBox.setHgrow(titelSection.getChildren().get(3), Priority.ALWAYS);
        titelSection.setAlignment(Pos.CENTER);

        centralContainer.setTop(titelSection);
        BorderPane.setMargin(titelSection, new Insets(80, 40, 0, 40));
        setCenter(centralContainer);

        content.setMaxSize(700,500);
        content.setPrefSize(700,500);
        content.setSpacing(10);
        content.setAlignment(Pos.CENTER);

        loginButtons.setMaxSize(300,500);
        loginButtons.setPrefSize(300,500);
        loginButtons.setSpacing(15);
        loginButtons.setAlignment(Pos.CENTER);

        playerOneInfo.setMaxSize(300,600);
        playerOneInfo.setPrefSize(300,600);
        playerOneInfo.setSpacing(15);
        playerOneInfo.setAlignment(Pos.CENTER);

        playerTwoInfo.setMaxSize(300,500);
        playerTwoInfo.setPrefSize(300,500);
        playerTwoInfo.setSpacing(15);
        playerTwoInfo.setAlignment(Pos.CENTER);

        playerOnePfp.setFitWidth(120);
        playerOnePfp.setFitHeight(120);

        playerTwoPfp.setFitWidth(120);
        playerTwoPfp.setFitHeight(120);

        Label playerOneTitle = new Label("player one");
        playerOneTitle.setAlignment(Pos.CENTER);
        playerOneTitle.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        playerOneTitle.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        playerOneName.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        playerOneName.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        playerOneInfo.getChildren().addAll(playerOneTitle, playerOnePfp, playerOneName);


        Label playerTwoTitleInfo = new Label("player two");
        playerTwoTitleInfo.setAlignment(Pos.CENTER);
        playerTwoTitleInfo.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        playerTwoTitleInfo.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        playerTwoName.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        playerTwoName.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        playerTwoInfo.getChildren().addAll(playerTwoTitleInfo, playerTwoPfp, playerTwoName);


        Label playerTwoTitleLogin = new Label("player two");
        playerTwoTitleLogin.setAlignment(Pos.CENTER);
        playerTwoTitleLogin.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        playerTwoTitleLogin.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        loginButtons.getChildren().addAll(playerTwoTitleInfo,loginPlayerTwo, createAccountPlayerTwo);

        Label verticalSpacer = new Label("VS");
        verticalSpacer.setAlignment(Pos.CENTER);
        verticalSpacer.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        verticalSpacer.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
        verticalSpacer.setMaxSize(80,600);
        verticalSpacer.setPrefSize(80,600);

        content.getChildren().addAll(playerOneInfo, verticalSpacer, loginButtons);
        centralContainer.setCenter(content);

        startGame.setMaxSize(80,40);
        centralContainer.setBottom(startGame);
        BorderPane.setAlignment(startGame, Pos.CENTER);
        BorderPane.setMargin(startGame, new Insets(0, 0, 40, 0));

        this.setStyle("-fx-background-color: " + this.resourceManager.getTheme().getColor() + ";");
    }

    private Region createSpacer(Button match) {
        Region r = new Region();
        r.prefWidthProperty().bind(match.widthProperty());
        return r;
    }

    Button getReturnButton() {
        return returnButton;
    }

    HBox getContent() {
        return content;
    }

    VBox getPlayerOneInfo() {
        return playerOneInfo;
    }

    VBox getPlayerTwoInfo() {
        return playerTwoInfo;
    }

    VBox getLoginButtons() {
        return loginButtons;
    }

    Button getStartGame() {
        return startGame;
    }

    Button getCreateAccountPlayerTwo() {
        return createAccountPlayerTwo;
    }

    Button getLoginPlayerTwo() {
        return loginPlayerTwo;
    }

    Label getPlayerTwoName() {
        return playerTwoName;
    }

    ImageView getPlayerTwoPfp() {
        return playerTwoPfp;
    }

    Label getPlayerOneName() {
        return playerOneName;
    }

    ImageView getPlayerOnePfp() {
        return playerOnePfp;
    }

    ResourceManager getResourceManager() {
        return resourceManager;
    }

}