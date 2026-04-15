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
    private Label hostColorLabel;
    private ImageView hostColor;

    public MultiPlayerGuestView(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        this.returnButton = new TextButton(resourceManager, "←");

        this.roomCodeInput = new TextField();
        this.roomCodeInput.setPromptText("VOER CODE IN");
        this.roomCodeInput.setMaxWidth(250);
        this.joinButton = new LongButtonComponent(resourceManager, "JOIN LOBBY");
        this.statusLabel = new Label("VUL EEN CODE IN");

        this.guestPfpView = createProfilePictureView();
        this.guestName = new Label("Jij (Gast)");
        this.guestColorPicker = new ImageSliderComponent(resourceManager);

        this.hostPfpView = createProfilePictureView();
        this.hostName = new Label("Wachten op join...");
        this.hostColorLabel = new Label("KLEUR: ?");
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

        Label title = new Label("JOIN GAME");
        title.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        title.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        HBox titelSection = new HBox(returnButton, new Region(), title, new Region(), createSpacer(returnButton));
        HBox.setHgrow(titelSection.getChildren().get(1), Priority.ALWAYS);
        HBox.setHgrow(titelSection.getChildren().get(3), Priority.ALWAYS);
        titelSection.setAlignment(Pos.CENTER);
        titelSection.setPadding(new Insets(80, 40, 0, 40));
        centralContainer.setTop(titelSection);

        VBox hostInfo = new VBox(20, createHeader("HOST"), hostPfpView, hostName,hostColor);
        hostInfo.setAlignment(Pos.TOP_CENTER);

        VBox centerControls = new VBox(20, roomCodeInput, joinButton, statusLabel);
        centerControls.setAlignment(Pos.CENTER);

        VBox guestInfo = new VBox(20, createHeader("YOUR SETUP"), guestPfpView, guestName, guestColorPicker);
        guestInfo.setAlignment(Pos.TOP_CENTER);

        HBox content = new HBox(50, hostInfo, centerControls, guestInfo);
        content.setAlignment(Pos.CENTER);
        centralContainer.setCenter(content);

        this.setCenter(centralContainer);
        this.setStyle("-fx-background-color: " + resourceManager.getTheme().getColor() + ";");

        styleLabel(hostName);
        styleLabel(guestName);
        styleLabel(hostColorLabel);
        styleLabel(statusLabel);
    }

    private ImageView createProfilePictureView() {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
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
        l.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));
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
    Label getHostColorLabel() { return hostColorLabel; }

    ResourceManager getResourceManager() {
        return resourceManager;
    }

    ImageView getHostColor() {
        return hostColor;
    }
}