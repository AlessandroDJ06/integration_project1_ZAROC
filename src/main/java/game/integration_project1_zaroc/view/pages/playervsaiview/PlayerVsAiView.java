package game.integration_project1_zaroc.view.pages.playervsaiview;

import game.integration_project1_zaroc.view.components.buttons.TextButton;
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

public class PlayerVsAiView extends BorderPane {
    private ResourceManager resourceManager;
    private TextButton returnButton;
    private Label specialtyLabel;
    private Label selectedAiNameLabel;
    private GridPane aiGrid;

    public PlayerVsAiView(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        this.returnButton = new TextButton(resourceManager, "←");
        this.selectedAiNameLabel = new Label("KIES EEN TEGENSTANDER");
        this.selectedAiNameLabel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));

        this.specialtyLabel = new Label("Bekijk hun unieke speelstijl...");
        this.specialtyLabel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));

        this.aiGrid = new GridPane();
    }

    void layoutNodes() {
        this.getChildren().clear();
        returnButton.updateLayout();
        BorderPane centralContainer = new BorderPane();
        centralContainer.setBackground(new Background(new BackgroundImage(
                resourceManager.getImage(Components.LARGECONTAINER),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, true, false)
        )));
        centralContainer.setMaxSize(800, 640);
        centralContainer.setPrefSize(800, 640);

        Label title = new Label("SELECTEER TEGENSTANDER");
        title.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        title.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        HBox titelSection = new HBox(returnButton, new Region(), title, new Region(), createSpacer(returnButton));
        HBox.setHgrow(titelSection.getChildren().get(1), Priority.ALWAYS);
        HBox.setHgrow(titelSection.getChildren().get(3), Priority.ALWAYS);
        titelSection.setAlignment(Pos.CENTER);

        centralContainer.setTop(titelSection);
        BorderPane.setMargin(titelSection, new Insets(80, 40, 0, 40));

        aiGrid.setAlignment(Pos.CENTER);
        aiGrid.setHgap(40);
        aiGrid.setVgap(10);

        VBox infoBox = new VBox(selectedAiNameLabel, specialtyLabel);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setSpacing(8);
        infoBox.setPadding(new Insets(10, 0, 45, 0));

        VBox contentWrapper = new VBox(aiGrid, infoBox);
        contentWrapper.setAlignment(Pos.CENTER);
        contentWrapper.setSpacing(15);
        centralContainer.setCenter(contentWrapper);

        this.setCenter(centralContainer);
        BorderPane.setMargin(centralContainer, new Insets(65, 0, 0, 0));
        this.setStyle("-fx-background-color: " + this.resourceManager.getTheme().getColor() + ";");
        updateLabelColors();
    }

    public Button addAiButton(ProfilePictures profile, int col, int row) {
        Button btn = new Button();

        ImageView pfpView = new ImageView(resourceManager.getProfilePicture(profile));
        pfpView.setFitWidth(75);
        pfpView.setFitHeight(75);
        pfpView.setPreserveRatio(true);

        StackPane pfpFrame = new StackPane(pfpView);
        pfpFrame.setMinSize(85, 85);
        pfpFrame.setMaxSize(85, 85);

        Color borderColor = Color.web(resourceManager.getTheme().getTextColor());

        pfpFrame.setBackground(new Background(new BackgroundFill(
                borderColor,
                new CornerRadii(0),
                Insets.EMPTY
        )));

        btn.setGraphic(pfpFrame);
        btn.setBackground(Background.EMPTY);
        btn.setPadding(new Insets(5));
        setButtonStyle(btn, false);

        aiGrid.add(btn, col, row);
        return btn;
    }

    public void addDifficultyLabel(String text, int row) {
        Label label = new Label(text);
        label.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));
        label.setTextFill(Color.web(resourceManager.getTheme().getTextColor(), 0.6));
        label.setMinWidth(100);
        aiGrid.add(label, 0, row);
    }

    public void setButtonStyle(Button btn, boolean isHovering) {
        CornerRadii radii = new CornerRadii(12);
        if (isHovering) {
            btn.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, radii, new BorderWidths(3))));
            btn.setBackground(new Background(new BackgroundFill(Color.web("#ffffff", 0.15), radii, Insets.EMPTY)));
        } else {
            btn.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, radii, new BorderWidths(3))));
            btn.setBackground(Background.EMPTY);
        }
    }

    public void setSelectionText(String name, String specialty) {
        selectedAiNameLabel.setText(name);
        specialtyLabel.setText(specialty);
    }

    private void updateLabelColors() {
        String color = resourceManager.getTheme().getTextColor();
        selectedAiNameLabel.setTextFill(Color.web(color));
        specialtyLabel.setTextFill(Color.web(color));
    }

    private Region createSpacer(Button match) {
        Region r = new Region();
        r.prefWidthProperty().bind(match.widthProperty());
        return r;
    }

    public TextButton getReturnButton() { return returnButton; }
    public ResourceManager getResourceManager() { return resourceManager; }
}