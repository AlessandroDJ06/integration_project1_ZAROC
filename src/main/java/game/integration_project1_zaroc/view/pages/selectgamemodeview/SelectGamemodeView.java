package game.integration_project1_zaroc.view.pages.selectgamemodeview;

import game.integration_project1_zaroc.view.components.buttons.GeneralActionsComponent;
import game.integration_project1_zaroc.view.components.buttons.LongButtonComponent;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class SelectGamemodeView extends BorderPane {
    private ResourceManager resourceManager;

    private Button playerVsPlayerButton;
    private Button playerVsAiButton;
    private Button multiPlayerButton;
    private LongButtonComponent unfinishedGamesButton;
    private LongButtonComponent leaderBoardButton;

    private GeneralActionsComponent settingsButton;
    private GeneralActionsComponent infoButton;
    private GeneralActionsComponent profileButton;


    public SelectGamemodeView(ResourceManager resourceManager){
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes(){
        this.playerVsPlayerButton = new Button();
        this.playerVsAiButton = new Button();
        this.multiPlayerButton = new Button();
        this.unfinishedGamesButton = new LongButtonComponent(resourceManager,"UNFINISHED GAMES");
        this.leaderBoardButton = new LongButtonComponent(resourceManager,"LEADERBOARD");

        this.settingsButton = new GeneralActionsComponent(this.resourceManager, Components.SETTINGS);
        this.infoButton = new GeneralActionsComponent(this.resourceManager,Components.RULES);
        this.profileButton = new GeneralActionsComponent(this.resourceManager,Components.PROFILE);

    }

    void layoutNodes(){
        settingsButton.updateLayout();
        infoButton.updateLayout();
        profileButton.updateLayout();
        unfinishedGamesButton.updateLayout();
        BorderPane centralContainer = new BorderPane();
        Image boardBackgroundImage = resourceManager.getImage(Components.LARGECONTAINER);
        BackgroundSize backgroundSize = new BackgroundSize(600, 440, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(
                boardBackgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );
        centralContainer.setBackground(new Background(backgroundImage));
        centralContainer.setMaxSize(800, 640);
        centralContainer.setPadding(new Insets(20,0,0,0));
        setCenter(centralContainer);
        BorderPane.setAlignment(centralContainer, Pos.CENTER);


        Label title = new Label("SELECT GAMEMODE");
        title.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        title.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        centralContainer.setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);

        BorderPane.setMargin(title, new Insets(60, 0, 0, 0));


        playerVsPlayerButton.setGraphic(new ImageView(resourceManager.getImage(Components.PLAYERVPLAYER)));
        playerVsPlayerButton.setBackground(Background.EMPTY);
        Label playerVsPlayerLabel = new Label("Player Vs Player");
        playerVsPlayerLabel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));
        playerVsPlayerLabel.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
        VBox playerVsPlayerSection = new VBox(playerVsPlayerLabel, playerVsPlayerButton);
        playerVsPlayerSection.setSpacing(15);
        playerVsPlayerSection.setAlignment(Pos.CENTER);

        playerVsAiButton.setGraphic(new ImageView(resourceManager.getImage(Components.PLAYERVAI)));
        playerVsAiButton.setBackground(Background.EMPTY);
        Label playerVsAiLabel = new Label("Player Vs Ai");
        playerVsAiLabel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));
        playerVsAiLabel.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
        VBox playerVsAiSection = new VBox(playerVsAiLabel, playerVsAiButton);
        playerVsAiSection.setAlignment(Pos.CENTER);
        playerVsAiSection.setSpacing(15);

        multiPlayerButton.setGraphic(new ImageView(resourceManager.getImage(Components.MULTIPLAYER)));
        multiPlayerButton.setBackground(Background.EMPTY);
        Label playerVsPlayerMultiplayerLabel = new Label("Multiplayer");
        playerVsPlayerMultiplayerLabel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));
        playerVsPlayerMultiplayerLabel.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
        VBox playerVsPlayerMultiplayerSection = new VBox(playerVsPlayerMultiplayerLabel, multiPlayerButton);
        playerVsPlayerMultiplayerSection.setSpacing(15);
        playerVsPlayerMultiplayerSection.setAlignment(Pos.CENTER);

        HBox gameModeSelectors = new HBox(playerVsPlayerMultiplayerSection,playerVsPlayerSection, playerVsAiSection);

        Label seperator = new Label("-- or --");
        seperator.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        seperator.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        VBox contentVbox = new VBox(gameModeSelectors,seperator,unfinishedGamesButton);

        gameModeSelectors.setAlignment(Pos.CENTER);
        gameModeSelectors.setSpacing(40);

        contentVbox.setAlignment(Pos.CENTER);
        contentVbox.setSpacing(15);

        centralContainer.setCenter(contentVbox);
        BorderPane.setMargin(gameModeSelectors, new Insets(0, 0, 40, 0));

        VBox infoAndSettingsVbox = new VBox(this.settingsButton, this.infoButton);
        setRight(infoAndSettingsVbox);
        BorderPane.setAlignment(infoAndSettingsVbox, Pos.TOP_RIGHT);
        infoAndSettingsVbox.setPadding(new Insets(30, 30, 0, 0));
        infoAndSettingsVbox.setSpacing(15);

        VBox profileButtonVbox = new VBox(this.profileButton);
        setLeft(profileButtonVbox);
        BorderPane.setAlignment(profileButtonVbox, Pos.TOP_LEFT);
        profileButtonVbox.setPadding(new Insets(30, 0, 0, 30));

        leaderBoardButton.updateLayout();
        leaderBoardButton.setMaxSize(60,20);
        setBottom(leaderBoardButton);
        BorderPane.setAlignment(leaderBoardButton,Pos.CENTER_RIGHT);
        leaderBoardButton.setPadding(new Insets(10,50,40,0));

        this.setStyle("-fx-background-color: " + this.resourceManager.getTheme().getColor() + ";");
    }

    ResourceManager getResourceManager() {
        return resourceManager;
    }

    Button getProfileButton() {
        return profileButton;
    }

    Button getInfoButton() {
        return infoButton;
    }

    Button getSettingsButton() {
        return settingsButton;
    }

    LongButtonComponent getUnfinishedGamesButton() {
        return unfinishedGamesButton;
    }

    Button getPlayerVsAiButton() {
        return playerVsAiButton;
    }

    Button getPlayerVsPlayerButton() {
        return playerVsPlayerButton;
    }

    Button getMultiPlayerButton(){
        return multiPlayerButton;
    }

    Button getLeaderboardButton(){
        return leaderBoardButton; }
    }

