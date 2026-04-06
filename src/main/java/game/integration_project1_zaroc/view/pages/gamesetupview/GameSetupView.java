package game.integration_project1_zaroc.view.pages.gamesetupview;

import game.integration_project1_zaroc.view.components.buttons.GeneralActionsComponent;
import game.integration_project1_zaroc.view.components.buttons.TextButton;
import game.integration_project1_zaroc.view.components.slidercomponents.ImageSliderComponent;
import game.integration_project1_zaroc.view.components.buttons.LongButtonComponent;
import game.integration_project1_zaroc.view.components.slidercomponents.TextSliderComponent;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;
import game.integration_project1_zaroc.view.sharedlogic.utils.LayoutHelpers;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class GameSetupView extends BorderPane {
    private ResourceManager resourceManager;

    private Button profileButton;
    private Button settingsButton;
    private Button infoButton;
    private Button leaderBoardButton;
    private Button returnButton;
    private Button createGameButton;
    private ImageSliderComponent colorPickerOne;
    private ImageSliderComponent colorPickerTwo;
    private TextSliderComponent startingPlayerPicker;

    public GameSetupView(ResourceManager resourceManager){
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    public void initialiseNodes(){
        this.infoButton = new GeneralActionsComponent(this.resourceManager, Components.RULES);
        this.settingsButton = new GeneralActionsComponent(this.resourceManager,Components.SETTINGS);
        this.profileButton = new GeneralActionsComponent(this.resourceManager,Components.PROFILE);
        this.returnButton = new TextButton(resourceManager,"←");
        this.createGameButton = new TextButton(resourceManager,"START");
        this.leaderBoardButton = new LongButtonComponent(this.resourceManager,"Leaderboard");
        this.colorPickerOne = new ImageSliderComponent(resourceManager);
        this.colorPickerTwo = new ImageSliderComponent(resourceManager);
        this.startingPlayerPicker = new TextSliderComponent(resourceManager);
    }

    public void layoutNodes(){
        //center container (settings)
        BorderPane centraContainer = new BorderPane();
        centraContainer.setBackground(new Background(LayoutHelpers.setBackground(resourceManager,100,100)));
        centraContainer.setMaxSize(500,500);
        centraContainer.setMinSize(500,500);
        BorderPane.setMargin(centraContainer,new Insets(100,0,0,0));

        //buttons in the container (start game and return)
        returnButton.setMaxSize(40,40);
        HBox titelSection = new HBox();
        titelSection.setAlignment(Pos.CENTER);
        titelSection.setPadding(new Insets(15));

        centraContainer.setBottom(createGameButton);
        BorderPane.setAlignment(createGameButton,Pos.TOP_CENTER);
        createGameButton.setPadding(new Insets(0,0,15,0));

        //title section (game settings)
        //title property's
        Label title = new Label("Game Settings");
        title.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        title.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
        title.setPadding(new Insets(30,0,0,0));

        //layout title section
        //spacers for layout
        Region rightSpacer = new Region();
        rightSpacer.prefWidthProperty().bind(returnButton.widthProperty());
        Region leftFiller = new Region();
        Region rightFiller = new Region();
        HBox.setHgrow(leftFiller, Priority.ALWAYS);
        HBox.setHgrow(rightFiller, Priority.ALWAYS);

        //padding and allignment for title section
        titelSection.setPadding(new Insets(15));
        centraContainer.setTop(titelSection);
        titelSection.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(titelSection,Pos.CENTER);

        //place title section
        titelSection.getChildren().addAll(returnButton, leftFiller, title, rightFiller, rightSpacer);
        centraContainer.setTop(titelSection);



        //settings selectors
        VBox innerRows = new VBox(
                createSettingRow("Starting player: ", startingPlayerPicker),
                createSettingRow("Player1 color  : ", colorPickerOne),
                createSettingRow("Player2 color  : ", colorPickerTwo)
        );
        innerRows.setAlignment(Pos.CENTER);
        innerRows.setSpacing(20);
        innerRows.setPadding(new Insets(0, 0, 20, 0));
        centraContainer.setCenter(innerRows);
        setCenter(centraContainer);

        //buttons outside of the central container
        //info and settings
        VBox infoAndSettingsVbox = new VBox(this.settingsButton,this.infoButton);
        setRight(infoAndSettingsVbox);
        BorderPane.setAlignment(infoAndSettingsVbox, Pos.TOP_RIGHT);
        infoAndSettingsVbox.setPadding(new Insets(30,30,0,0));
        infoAndSettingsVbox.setSpacing(15);

        //profile buttons
        VBox profileButtonVbox = new VBox(this.profileButton);
        setLeft(profileButtonVbox);
        BorderPane.setAlignment(profileButtonVbox,Pos.TOP_LEFT);
        profileButtonVbox.setPadding(new Insets(30,0,0,30));

        //leaderBoard button
        leaderBoardButton.setMaxSize(100,40);
        setBottom(leaderBoardButton);
        BorderPane.setAlignment(leaderBoardButton,Pos.CENTER_RIGHT);
        leaderBoardButton.setPadding(new Insets(10,50,40,40));




        this.setStyle("-fx-background-color: " + this.resourceManager.getTheme().getColor() + ";");
    }

    private HBox createSettingRow(String labelText, Node component) {
        Label label = new Label(labelText);
        label.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        label.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
        label.setMinWidth(250);
        label.setMinHeight(60);

        HBox row = new HBox(label, component);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    Button getInfoButton() {
        return infoButton;
    }

    Button getSettingsButton() {
        return settingsButton;
    }

    Button getProfileButton() {
        return profileButton;
    }

    ImageSliderComponent getColorPickerTwo() {
        return colorPickerTwo;
    }

    ImageSliderComponent getColorPickerOne() {
        return colorPickerOne;
    }

    ResourceManager getResourceManager() {
        return resourceManager;
    }

    Button getLeaderBoardButton() {
        return leaderBoardButton;
    }

    Button getReturnButton(){
        return this.returnButton;
    }

    Button getCreateGameButton(){
        return this.createGameButton;
    }


    TextSliderComponent getStartingPlayerPicker() {
        return startingPlayerPicker;
    }
}
