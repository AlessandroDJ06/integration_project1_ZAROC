package game.integration_project1_zaroc.view.pages.loginview;

import game.integration_project1_zaroc.view.components.buttons.LongButtonComponent;
import game.integration_project1_zaroc.view.components.buttons.TextButton;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;
import game.integration_project1_zaroc.view.sharedlogic.utils.LayoutHelpers;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class LoginView extends BorderPane {
    private ResourceManager resourceManager;
    private Button returnButton;
    private Button loginButton;
    private Button playAsGuest;

    private TextField nameField;
    private PasswordField passwordField;

    public LoginView(ResourceManager resourceManager){
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes(){
        this.returnButton = new TextButton(resourceManager,"←");
        this.loginButton = new TextButton(resourceManager,"LOGIN");

        this.nameField = new TextField();
        this.nameField.setPromptText("Typ je username...");

        this.passwordField = new PasswordField();
        this.passwordField.setPromptText("Typ je wachtwoord...");

        this.playAsGuest = new TextButton(resourceManager,"GAST LOGIN");
    }

    private void layoutNodes(){
        setBackground(new Background(LayoutHelpers.setBackground(this.resourceManager,550,650)));
        this.setPrefSize(550,650);
        this.setMaxSize(550,650);
        this.setPadding(new Insets(60, 40, 40, 40));

        // === Top Section (Titelbalk) ===
        returnButton.setMaxSize(40,40);
        HBox titelSection = new HBox();
        titelSection.setAlignment(Pos.CENTER);
        titelSection.setPadding(new Insets(15));

        Label title = new Label("LOGIN");
        title.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        title.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        Region rightSpacer = new Region();
        rightSpacer.prefWidthProperty().bind(returnButton.widthProperty());
        Region leftFiller = new Region();
        Region rightFiller = new Region();
        HBox.setHgrow(leftFiller, Priority.ALWAYS);
        HBox.setHgrow(rightFiller, Priority.ALWAYS);

        titelSection.getChildren().addAll(returnButton, leftFiller, title, rightFiller, rightSpacer);
        setTop(titelSection);

        VBox centerContainer = new VBox();
        centerContainer.setAlignment(Pos.CENTER);

        centerContainer.setSpacing(40);
        centerContainer.setMaxWidth(260);

        Background background = new Background(new BackgroundImage(
                resourceManager.getImage(Components.INPUTFIELD),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(260, 40, false, false, false, false)
        ));

        nameField.setBackground(background);
        passwordField.setBackground(background);

        nameField.setPrefHeight(40);
        nameField.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));

        passwordField.setPrefHeight(40);
        passwordField.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));

        Label usernameLabel = new Label("Username:");
        usernameLabel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        usernameLabel.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        VBox nameBox = new VBox(5);
        nameBox.setAlignment(Pos.CENTER_LEFT);
        nameBox.getChildren().addAll(usernameLabel, nameField);


        Label passwordLabel = new Label("Wachtwoord:");
        passwordLabel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        passwordLabel.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        Label spacer = new Label("--of--");
        spacer.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        spacer.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));




        VBox passBox = new VBox(5);
        passBox.setAlignment(Pos.CENTER_LEFT);
        passBox.getChildren().addAll(passwordLabel, passwordField);
        centerContainer.getChildren().addAll(nameBox, passBox,loginButton,spacer,playAsGuest);

        setCenter(centerContainer);
        loginButton.setPadding(new Insets(0, 0, 0, 0));
    }

    String getUsername() {
        return nameField.getText();
    }

    String getPassword() {
        return passwordField.getText();
    }

    Button getLoginButton() {
        return loginButton;
    }

    Button getReturnButton() {
        return returnButton;
    }

    Button getPlayAsGuest() {
        return playAsGuest;
    }
}