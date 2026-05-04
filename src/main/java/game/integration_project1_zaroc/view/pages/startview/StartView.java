package game.integration_project1_zaroc.view.pages.startview;

import game.integration_project1_zaroc.view.components.buttons.GeneralActionsComponent;
import game.integration_project1_zaroc.view.components.buttons.LongButtonComponent;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class StartView extends BorderPane {
    private ResourceManager resourceManager;

    private LongButtonComponent loginButton;
    private LongButtonComponent createAccountButton;
    private LongButtonComponent leaderboardButton;
    private GeneralActionsComponent settingsButton;
    private GeneralActionsComponent infoButton;



    public StartView(ResourceManager resourceManager){
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes(){
        this.createAccountButton = new LongButtonComponent(resourceManager,"CREATE ACCOUNT");
        this.loginButton = new LongButtonComponent(resourceManager,"LOGIN");
        this.leaderboardButton = new LongButtonComponent(resourceManager,"LEADERBOARD");
        this.settingsButton = new GeneralActionsComponent(resourceManager, Components.SETTINGS);
        this.infoButton = new GeneralActionsComponent(resourceManager,Components.RULES);
    }

    void layoutNodes(){
        settingsButton.updateLayout();
        infoButton.updateLayout();
        loginButton.updateLayout();
        createAccountButton.updateLayout();
        leaderboardButton.updateLayout();

        VBox infoAndSettingsVbox = new VBox(this.settingsButton,this.infoButton);
        setRight(infoAndSettingsVbox);
        BorderPane.setAlignment(infoAndSettingsVbox, Pos.TOP_RIGHT);
        infoAndSettingsVbox.setPadding(new Insets(30,30,0,0));
        infoAndSettingsVbox.setSpacing(15);

        Region leftSpacer = new Region();
        leftSpacer.prefWidthProperty().bind(infoAndSettingsVbox.widthProperty());
        setLeft(leftSpacer);

        Label title = new Label("ZAROC");
        title.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLEBIG));
        title.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
        VBox centralSection = new VBox(title,loginButton,createAccountButton,leaderboardButton);
        centralSection.setMaxSize(600,600);

        centralSection.setSpacing(15);
        centralSection.setAlignment(Pos.CENTER);
        setCenter(centralSection);

        BorderPane.setAlignment(centralSection,Pos.CENTER);


        this.setStyle("-fx-background-color: " + this.resourceManager.getTheme().getColor() + ";");
    }

    LongButtonComponent getLoginButton() {
        return loginButton;
    }
    GeneralActionsComponent getInfoButton() {
        return infoButton;
    }

    GeneralActionsComponent getSettingsButton() {
        return settingsButton;
    }

    LongButtonComponent getLeaderboardButton() {
        return leaderboardButton;
    }

    LongButtonComponent getCreateAccountButton() {
        return createAccountButton;
    }

    ResourceManager getResourceManager() {
        return resourceManager;
    }
}