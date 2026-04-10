package game.integration_project1_zaroc.view.pages.createaccountview;

import game.integration_project1_zaroc.view.components.buttons.TextButton;
import game.integration_project1_zaroc.view.components.slidercomponents.ImageSliderComponent;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;
import game.integration_project1_zaroc.view.sharedlogic.utils.LayoutHelpers;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class CreateAccountView extends BorderPane {
    private ResourceManager resourceManager;
    private Button returnButton;
    private Button createButton;

    private ImageSliderComponent profilePicturePicker;
    private TextField nameField;
    private TextField emailField;
    private PasswordField passwordField;

    public CreateAccountView(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        this.returnButton = new TextButton(resourceManager, "←");
        this.createButton = new TextButton(resourceManager, "MAAK AAN");

        this.profilePicturePicker = new ImageSliderComponent(this.resourceManager);

        this.nameField = new TextField();
        this.nameField.setPromptText("Typ je username...");

        this.emailField = new TextField();
        this.emailField.setPromptText("Typ je email...");

        this.passwordField = new PasswordField();
        this.passwordField.setPromptText("Typ je password...");
    }

    private void layoutNodes() {
        setBackground(new Background(LayoutHelpers.setBackground(this.resourceManager, 550, 650)));
        this.setPrefSize(550, 650);
        this.setMaxSize(550, 650);
        this.setPadding(new Insets(60, 40, 40, 40));

        // === Top ===
        returnButton.setMaxSize(40, 40);
        HBox titelSection = new HBox();
        titelSection.setAlignment(Pos.CENTER);
        titelSection.setPadding(new Insets(15));

        Label title = new Label("ACCOUNT");
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
        centerContainer.setMaxWidth(260);

        Background background = new Background(new BackgroundImage(
                resourceManager.getImage(Components.INPUTFIELD),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(260, 40, false, false, false, false)
        ));

        nameField.setBackground(background);
        emailField.setBackground(background);
        passwordField.setBackground(background);

        nameField.setPrefHeight(40);
        nameField.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));

        emailField.setPrefHeight(40);
        emailField.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));

        passwordField.setPrefHeight(40);
        passwordField.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));

        centerContainer.getChildren().addAll(
                profilePicturePicker,
                labeledField("Username:", nameField),
                labeledField("Email:", emailField),
                labeledField("Password:", passwordField)
        );

        profilePicturePicker.getImageView().setFitHeight(90);
        profilePicturePicker.getImageView().setFitWidth(90);

        setCenter(centerContainer);

        // === Bottom ===
        VBox bottomSection = new VBox(createButton);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(0, 0, 30, 0));
        setBottom(bottomSection);
    }

    private VBox labeledField(String labelText, Control field) {
        Label label = new Label(labelText);
        label.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        label.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        VBox box = new VBox(5, label, field);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    String getUsername() { return nameField.getText(); }
    String getEmail() { return emailField.getText(); }
    String getPassword() { return passwordField.getText(); }
    Button getCreateButton() { return createButton; }
    Button getReturnButton() { return returnButton; }
    ImageSliderComponent getProfilePicturePicker() { return profilePicturePicker; }
    ResourceManager getResourceManager() { return resourceManager; }
}