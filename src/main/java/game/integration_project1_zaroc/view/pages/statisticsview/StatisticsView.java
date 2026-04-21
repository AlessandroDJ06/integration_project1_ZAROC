package game.integration_project1_zaroc.view.pages.statisticsview;

import game.integration_project1_zaroc.view.components.buttons.TextButton;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.profilePictures.ProfilePictures;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class StatisticsView extends BorderPane {
    private ResourceManager resourceManager;
    private Label playerProfileTitle;
    private ImageView profilePicture;
    private Label accountInfoTitle;
    private Text accountInfo;
    private Label preferencesTitle;
    private Text preferencesText;
    private Label gameStatsTitle;
    private Text gameStatsText;
    private Button returnButton;
    private VBox wholeVBox;
    private Label guestLabel;

    public StatisticsView(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        playerProfileTitle = new Label("PLAYER PROFILE");
        profilePicture = new ImageView(resourceManager.getProfilePicture(ProfilePictures.EMPTY));
        accountInfoTitle = new Label("Account Info");
        accountInfo = new Text("Username/Email");
        preferencesTitle = new Label("Gameplay Preferences");
        preferencesText = new Text("Playstyle/Difficulty");
        gameStatsTitle = new Label("Lifetime Stats");
        gameStatsText = new Text("Total Games/Total Wins/Win Rate");
        returnButton = new TextButton(resourceManager, "←");
        guestLabel = new Label("You are playing as a guest,\nlog in to save stats!");
    }

    private void layoutNodes() {

        Image boardBackgroundImage = resourceManager.getImage(Components.PEGVIEW);
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(
                boardBackgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );

        Background background = new Background(backgroundImage);
        setBackground(background);
        this.setPrefSize(550, 550);
        this.setMaxSize(550, 550);

        playerProfileTitle.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        accountInfoTitle.setFont(resourceManager.getFont(Fonts.PRESSSTART2PMEDIUM));
        accountInfo.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));
        preferencesTitle.setFont(resourceManager.getFont(Fonts.PRESSSTART2PMEDIUM));
        preferencesText.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));
        gameStatsTitle.setFont(resourceManager.getFont(Fonts.PRESSSTART2PMEDIUM));
        gameStatsText.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));
        guestLabel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));

        profilePicture.setFitWidth(100);
        profilePicture.setFitHeight(100);

        returnButton.setMaxSize(40, 40);

        HBox accountInfoHBox = new HBox(profilePicture, accountInfo);
        accountInfoHBox.setAlignment(Pos.CENTER_LEFT);
        accountInfoHBox.setSpacing(10);

        StackPane header = new StackPane();
        header.getChildren().addAll(playerProfileTitle, returnButton);
        StackPane.setAlignment(returnButton, Pos.CENTER_LEFT);
        StackPane.setAlignment(playerProfileTitle, Pos.CENTER);
        wholeVBox = new VBox(header, createTitleHBox(accountInfoTitle), accountInfoHBox,
                createTitleHBox(preferencesTitle), preferencesText,
                createTitleHBox(gameStatsTitle), gameStatsText);
        accountInfo.setTextAlignment(TextAlignment.LEFT);
        preferencesText.setTextAlignment(TextAlignment.LEFT);
        gameStatsText.setTextAlignment(TextAlignment.LEFT);

        accountInfo.setWrappingWidth(300);
        accountInfo.setLineSpacing(20);

        preferencesText.setWrappingWidth(400);
        preferencesText.setLineSpacing(20);

        gameStatsText.setWrappingWidth(400);
        gameStatsText.setLineSpacing(20);


        wholeVBox.setAlignment(Pos.TOP_LEFT);
        wholeVBox.setSpacing(30);
        wholeVBox.setPadding(new Insets(40));

        this.setCenter(wholeVBox);


    }

    public HBox createTitleHBox(Label title) {
        Region leftLine = new Region();
        Region rightLine = new Region();

        Background black = new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY));

        leftLine.setBackground(black);
        leftLine.setMinHeight(2);
        leftLine.setMaxHeight(2);

        rightLine.setBackground(black);
        rightLine.setMinHeight(2);
        rightLine.setMaxHeight(2);

        HBox.setHgrow(leftLine, Priority.ALWAYS);
        HBox.setHgrow(rightLine, Priority.ALWAYS);

        HBox hBox = new HBox(10, leftLine, title, rightLine);
        hBox.setAlignment(Pos.CENTER);
        return hBox;
    }

    public void showGuestText() {
        wholeVBox.getChildren().clear();
        wholeVBox.getChildren().addAll(returnButton, guestLabel);

    }

    ResourceManager getResourceManager() {
        return resourceManager;
    }

    ImageView getProfilePicture() {
        return profilePicture;
    }


    Text getAccountInfo() {
        return accountInfo;
    }

    Text getPreferencesText() {
        return preferencesText;
    }

    Text getGameStatsText() {
        return gameStatsText;
    }

    Button getReturnButton() {
        return returnButton;
    }

}
