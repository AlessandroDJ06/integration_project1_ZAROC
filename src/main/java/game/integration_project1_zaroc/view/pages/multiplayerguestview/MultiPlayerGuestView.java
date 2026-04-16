package game.integration_project1_zaroc.view.pages.multiplayerguestview;

import game.integration_project1_zaroc.view.components.buttons.LongButtonComponent;
import game.integration_project1_zaroc.view.components.buttons.TextButton;
import game.integration_project1_zaroc.view.components.slidercomponents.ImageSliderComponent;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.profilePictures.ProfilePictures;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class MultiPlayerGuestView extends BorderPane {

    private ResourceManager resourceManager;
    private Button returnButton;

    private TextField roomCodeInput;
    private Button joinButton;
    private Label statusLabel;

    private ImageView guestPfpView;
    private Label guestName;
    private ImageSliderComponent guestColorPicker;

    private ImageView hostPfpView;
    private Label hostName;
    private ImageView hostColor;

    public MultiPlayerGuestView(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        this.returnButton = new TextButton(resourceManager, "←");

        this.roomCodeInput = new TextField();
        this.roomCodeInput.setPromptText("ENTER CODE");
        this.roomCodeInput.setMaxWidth(260);
        this.roomCodeInput.setPrefWidth(260);
        this.roomCodeInput.setPrefHeight(40);
        this.joinButton = new LongButtonComponent(resourceManager, "JOIN LOBBY");
        this.statusLabel = new Label("enter game code");

        this.guestPfpView = createProfilePictureView();
        this.guestName = new Label("Jij (Gast)");
        this.guestColorPicker = new ImageSliderComponent(resourceManager);

        this.hostPfpView = createProfilePictureView();
        this.hostName = new Label("Waiting");
        this.hostColor = new ImageView();
    }

    private void layoutNodes() {
        BorderPane centralContainer = new BorderPane();
        centralContainer.setBackground(new Background(new BackgroundImage(
                resourceManager.getImage(Components.LARGECONTAINER),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, true, false)
        )));
        centralContainer.setMaxSize(800, 640);
        centralContainer.setPrefSize(800, 640);

        Background background = new Background(new BackgroundImage(
                resourceManager.getImage(Components.INPUTFIELD),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(260, 40, false, false, false, false)
        ));

        roomCodeInput.setBackground(background);
        roomCodeInput.setStyle("-fx-text-fill:" + resourceManager.getTheme().getTextColor() +";");

        Label title = new Label("JOIN GAME");
        title.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        title.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        HBox titelSection = new HBox(returnButton, new Region(), title, new Region(), createSpacer(returnButton));
        HBox.setHgrow(titelSection.getChildren().get(1), Priority.ALWAYS);
        HBox.setHgrow(titelSection.getChildren().get(3), Priority.ALWAYS);
        titelSection.setAlignment(Pos.CENTER);
        titelSection.setPadding(new Insets(80, 40, 50, 40));
        centralContainer.setTop(titelSection);
        hostColor.setFitWidth(60);
        hostColor.setFitHeight(60);

        VBox hostInfo = new VBox(20, createHeader("HOST"), hostPfpView, hostName,hostColor);
        hostInfo.setMaxWidth(150);
        hostInfo.setPrefWidth(150);
        hostInfo.setAlignment(Pos.TOP_CENTER);

        VBox centerControls = new VBox(20, statusLabel,roomCodeInput );
        centerControls.setMaxWidth(260);
        centerControls.setPrefWidth(260);
        centerControls.setPadding(new Insets(50,0,0,0));
        centerControls.setAlignment(Pos.TOP_CENTER);

        centralContainer.setBottom(joinButton);
        BorderPane.setAlignment(joinButton,Pos.CENTER);
        joinButton.setPadding(new Insets(0,0,40,0));

        VBox guestInfo = new VBox(20, createHeader("YOU"), guestPfpView, guestName, guestColorPicker);
        guestInfo.setMaxWidth(150);
        guestInfo.setPrefWidth(150);
        guestInfo.setAlignment(Pos.TOP_CENTER);

        HBox content = new HBox(50, hostInfo, centerControls, guestInfo);
        content.setAlignment(Pos.CENTER);
        centralContainer.setCenter(content);

        this.setCenter(centralContainer);
        this.setStyle("-fx-background-color: " + resourceManager.getTheme().getColor() + ";");

        styleLabel(hostName);
        styleLabel(guestName);
        styleLabel(statusLabel);
    }

    private ImageView createProfilePictureView() {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(120);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);
        imageView.setImage(resourceManager.getProfilePicture(ProfilePictures.EMPTY));
        return imageView;
    }

    private Region createSpacer(Button match) {
        Region r = new Region();
        r.prefWidthProperty().bind(match.widthProperty());
        return r;
    }

    private Label createHeader(String text) {
        Label l = new Label(text);
        l.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        l.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
        return l;
    }

    private void styleLabel(Label l) {
        l.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSLIDER));
        l.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
    }

    Button getReturnButton() { return returnButton; }
    TextField getRoomCodeInput() { return roomCodeInput; }
    Button getJoinButton() { return joinButton; }
    Label getStatusLabel() { return statusLabel; }

    ImageView getGuestPfpView() { return guestPfpView; }
    Label getGuestName() { return guestName; }
    ImageSliderComponent getGuestColorPicker() { return guestColorPicker; }

    ImageView getHostPfpView() { return hostPfpView; }
    Label getHostName() { return hostName; }

    ResourceManager getResourceManager() {
        return resourceManager;
    }

    ImageView getHostColor() {
        return hostColor;
    }
}


