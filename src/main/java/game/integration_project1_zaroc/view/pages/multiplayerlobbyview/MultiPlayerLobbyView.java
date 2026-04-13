package game.integration_project1_zaroc.view.pages.multiplayerlobbyview;

import game.integration_project1_zaroc.view.components.buttons.LongButtonComponent;
import game.integration_project1_zaroc.view.components.buttons.TextButton;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class MultiPlayerLobbyView extends BorderPane {
    private ResourceManager resourceManager;
    private Button returnButton;
    private Button debugRoleButton;

    private Label roomCodeDisplayLabel;
    private TextField roomCodeInput;
    private Button joinButton;
    private Label statusLabel;

    private Label hostName;
    private Label hostColorLabel;

    private Label guestName;
    private Label guestColorLabel;

    private Button startGameButton;

    public MultiPlayerLobbyView(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        this.returnButton = new TextButton(resourceManager, "←");
        this.debugRoleButton = new Button("DEBUG: SWAP");

        this.roomCodeDisplayLabel = new Label("CODE: -----");
        this.roomCodeInput = new TextField();
        this.roomCodeInput.setPromptText("VOER CODE IN");
        this.joinButton = new LongButtonComponent(resourceManager, "JOIN");
        this.statusLabel = new Label("WACHTEN...");

        this.hostName = new Label("Wachten...");
        this.hostColorLabel = new Label("KLEUR: ZWART");

        this.guestName = new Label("Wachten...");
        this.guestColorLabel = new Label("KLEUR: WIT");

        this.startGameButton = new LongButtonComponent(resourceManager, "START");
    }

    private void layoutNodes() {
        BorderPane centralContainer = new BorderPane();
        centralContainer.setBackground(new Background(new BackgroundImage(
                resourceManager.getImage(Components.LARGECONTAINER),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, true, false)
        )));
        centralContainer.setMaxSize(800, 640);

        Label title = new Label("MULTIPLAYER LOBBY");
        title.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        title.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        HBox titelSection = new HBox(returnButton, debugRoleButton, new Region(), title, new Region(), new Region(){{setPrefWidth(50);}});
        HBox.setHgrow(titelSection.getChildren().get(2), Priority.ALWAYS);
        HBox.setHgrow(titelSection.getChildren().get(4), Priority.ALWAYS);
        titelSection.setAlignment(Pos.CENTER);
        titelSection.setPadding(new Insets(80, 40, 0, 40));
        centralContainer.setTop(titelSection);

        VBox hostInfo = new VBox(20, createHeader("HOST"), hostName, hostColorLabel);
        hostInfo.setAlignment(Pos.CENTER);

        VBox guestInfo = new VBox(20, createHeader("GUEST"), guestName, guestColorLabel);
        guestInfo.setAlignment(Pos.CENTER);

        VBox centerControls = new VBox(20, roomCodeDisplayLabel, roomCodeInput, joinButton, statusLabel);
        centerControls.setAlignment(Pos.CENTER);

        HBox content = new HBox(50, hostInfo, centerControls, guestInfo);
        content.setAlignment(Pos.CENTER);
        centralContainer.setCenter(content);

        centralContainer.setBottom(startGameButton);
        BorderPane.setAlignment(startGameButton, Pos.CENTER);
        BorderPane.setMargin(startGameButton, new Insets(0, 0, 60, 0));

        this.setCenter(centralContainer);
        this.setStyle("-fx-background-color: " + resourceManager.getTheme().getColor() + ";");

        styleLabel(hostName); styleLabel(guestName);
        styleLabel(hostColorLabel); styleLabel(guestColorLabel);
        styleLabel(roomCodeDisplayLabel); styleLabel(statusLabel);
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

    // Getters
    public Button getReturnButton() { return returnButton; }
    public Button getDebugRoleButton() { return debugRoleButton; }
    public Label getRoomCodeDisplayLabel() { return roomCodeDisplayLabel; }
    public TextField getRoomCodeInput() { return roomCodeInput; }
    public Button getJoinButton() { return joinButton; }
    public Label getStatusLabel() { return statusLabel; }
    public Label getHostName() { return hostName; }
    public Label getGuestName() { return guestName; }
    public Button getStartGameButton() { return startGameButton; }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }
}