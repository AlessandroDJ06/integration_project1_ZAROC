package game.integration_project1_zaroc.view.pages.multiplayerhostview;

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
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class MultiPlayerHostView extends BorderPane {
    private ResourceManager resourceManager;
    private Button returnButton;
    private Button startGameButton;

    private Label roomCodeDisplayLabel;
    private Label statusLabel;

    private ImageView hostPfpView;
    private Label hostName;
    private ImageSliderComponent hostColorPicker;

    private ImageView guestPfpView;
    private Label guestName;
    private Label guestColorLabel;

    public MultiPlayerHostView(ResourceManager resourceManager){
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes(){
        this.returnButton = new TextButton(resourceManager,"←");
        this.startGameButton = new LongButtonComponent(resourceManager, "START GAME");

        this.roomCodeDisplayLabel = new Label("CODE: GENERATING...");
        this.statusLabel = new Label("WAITING...");

        this.hostPfpView = createProfilePictureView();
        this.hostName = new Label("you (Host)");
        this.hostColorPicker = new ImageSliderComponent(resourceManager);

        this.guestPfpView = createProfilePictureView();
        this.guestName = new Label("Wachten...");
        this.guestColorLabel = new Label("KLEUR: ?");
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

        Label title = new Label("HOST GAME");
        title.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        title.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        HBox titelSection = new HBox(returnButton, new Region(), title, new Region(), createSpacer(returnButton));
        HBox.setHgrow(titelSection.getChildren().get(1), Priority.ALWAYS);
        HBox.setHgrow(titelSection.getChildren().get(3), Priority.ALWAYS);
        titelSection.setAlignment(Pos.CENTER);
        titelSection.setPadding(new Insets(80, 40, 40, 40));
        centralContainer.setTop(titelSection);

        VBox hostInfo = new VBox(20, createHeader("HOST"), hostPfpView, hostName, hostColorPicker);
        hostInfo.setAlignment(Pos.TOP_CENTER);

        VBox centerControls = new VBox(20, roomCodeDisplayLabel, statusLabel);
        centerControls.setAlignment(Pos.CENTER);

        VBox guestInfo = new VBox(20, createHeader("GUEST"), guestPfpView, guestName, guestColorLabel);
        guestInfo.setAlignment(Pos.TOP_CENTER);

        HBox content = new HBox(50, hostInfo, centerControls, guestInfo);
        content.setAlignment(Pos.CENTER);
        centralContainer.setCenter(content);

        centralContainer.setBottom(startGameButton);
        BorderPane.setAlignment(startGameButton, Pos.CENTER);
        BorderPane.setMargin(startGameButton, new Insets(0, 0, 60, 0));

        this.setCenter(centralContainer);
        this.setStyle("-fx-background-color: " + resourceManager.getTheme().getColor() + ";");

        // Styling toepassen op labels
        styleLabel(hostName);
        styleLabel(guestName);
        styleLabel(guestColorLabel);
        styleLabel(roomCodeDisplayLabel);
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

    public Button getReturnButton() { return returnButton; }
    public Button getStartGameButton() { return startGameButton; }
    public Label getRoomCodeDisplayLabel() { return roomCodeDisplayLabel; }
    public Label getStatusLabel() { return statusLabel; }

    public ImageView getHostPfpView() { return hostPfpView; }
    public Label getHostName() { return hostName; }
    public ImageSliderComponent getHostColorPicker() { return hostColorPicker; }

    public ImageView getGuestPfpView() { return guestPfpView; }
    public Label getGuestName() { return guestName; }
    public Label getGuestColorLabel() { return guestColorLabel; }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }
}