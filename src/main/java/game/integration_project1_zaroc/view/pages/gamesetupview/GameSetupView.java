package game.integration_project1_zaroc.view.pages.gamesetupview;

import game.integration_project1_zaroc.view.components.GeneralActionsComponent;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class GameSetupView extends BorderPane {
    private ResourceManager resourceManager;

    private Button profileButton;
    private Button settingsButton;
    private Button infoButton;

    public GameSetupView(ResourceManager resourceManager){
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    public void initialiseNodes(){
        this.infoButton = new GeneralActionsComponent(this.resourceManager, Components.RULES);
        this.settingsButton = new GeneralActionsComponent(this.resourceManager,Components.SETTINGS);
        this.profileButton = new GeneralActionsComponent(this.resourceManager,Components.PROFILE);
    }

    public void layoutNodes(){
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
}
