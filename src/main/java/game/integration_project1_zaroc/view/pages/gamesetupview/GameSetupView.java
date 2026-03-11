package game.integration_project1_zaroc.view.pages.gamesetupview;

import game.integration_project1_zaroc.view.components.GeneralActionsComponent;
import game.integration_project1_zaroc.view.components.ImageSliderComponent;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameSetupView extends BorderPane {
    private ResourceManager resourceManager;

    private Button profileButton;
    private Button settingsButton;
    private Button infoButton;
    private ImageSliderComponent colorPickerOne;
    private ImageSliderComponent colorPickerTwo;

    public GameSetupView(ResourceManager resourceManager){
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    public void initialiseNodes(){
        this.infoButton = new GeneralActionsComponent(this.resourceManager, Components.RULES);
        this.settingsButton = new GeneralActionsComponent(this.resourceManager,Components.SETTINGS);
        this.profileButton = new GeneralActionsComponent(this.resourceManager,Components.PROFILE);
        this.colorPickerOne = new ImageSliderComponent(resourceManager);
        this.colorPickerTwo = new ImageSliderComponent(resourceManager);
    }

    public void layoutNodes(){
        BorderPane centraContainer = new BorderPane();
        Image boardBackgroundImage = resourceManager.getImage(Components.PEGVIEW);
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(
                boardBackgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );

        centraContainer.setBackground(new Background(backgroundImage));
        centraContainer.setMaxSize(500,500);
        centraContainer.setMinSize(500,500);
        Label title = new Label("Game Settings");
        title.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        title.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
        centraContainer.setTop(title);
        BorderPane.setAlignment(title,Pos.TOP_CENTER);
        title.setPadding(new Insets(20,0,0,0));
           Label top = new Label("set difficulty : ");
        Label middle = new Label("player1 color  : ");
        Label bottom = new Label("player2 color  : ");

        HBox colorPickOne = new HBox(middle,colorPickerOne);
        HBox colorPickTwo = new HBox(bottom,colorPickerTwo);
        HBox difficulty = new HBox(top);


        VBox innerContainer = new VBox(difficulty,colorPickOne,colorPickTwo);
        innerContainer.setSpacing(15);
        innerContainer.setPadding(new Insets(0,0,0,20));

        for(Node hbox : innerContainer.getChildren()){
            if (hbox instanceof HBox){
                ((HBox) hbox).setAlignment(Pos.CENTER_LEFT);
                ((HBox) hbox).setMaxWidth(centraContainer.getMaxWidth());
                for (Node label : ((HBox) hbox).getChildren()){
                    if (label instanceof Label){
                        ((Label) label).setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
                        ((Label) label).setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
                    }
                }
            }
        }
        centraContainer.setCenter(innerContainer);
        BorderPane.setAlignment(innerContainer,Pos.CENTER);
        innerContainer.setAlignment(Pos.CENTER);
        innerContainer.setSpacing(20);
        setCenter(centraContainer);

        //buttons
        VBox infoAndSettingsVbox = new VBox(this.settingsButton,this.infoButton);
        setRight(infoAndSettingsVbox);
        BorderPane.setAlignment(infoAndSettingsVbox, Pos.TOP_RIGHT);
        infoAndSettingsVbox.setPadding(new Insets(30,30,0,0));
        infoAndSettingsVbox.setSpacing(15);
        VBox profileButtonVbox = new VBox(this.profileButton);
        setLeft(profileButtonVbox);
        BorderPane.setAlignment(profileButtonVbox,Pos.TOP_LEFT);
        profileButtonVbox.setPadding(new Insets(30,0,0,30));

        this.setStyle("-fx-background-color: " + this.resourceManager.getTheme().getColor() + ";");
    }

    public Button getInfoButton() {
        return infoButton;
    }

    public Button getSettingsButton() {
        return settingsButton;
    }

    public Button getProfileButton() {
        return profileButton;
    }

    public ImageSliderComponent getColorPickerTwo() {
        return colorPickerTwo;
    }

    public ImageSliderComponent getColorPickerOne() {
        return colorPickerOne;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }
}
