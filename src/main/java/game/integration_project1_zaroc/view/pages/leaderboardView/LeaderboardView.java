package game.integration_project1_zaroc.view.pages.leaderboardView;

import game.integration_project1_zaroc.view.components.buttons.TextButton;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.List;

public class LeaderboardView extends BorderPane {

    private TextButton returnButton;
    private ListView<String> leaderboardListView;
    private Label statusLabel;
    private ComboBox<String> sortDropdown;

    private final ResourceManager resourceManager;

    public LeaderboardView(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        returnButton        = new TextButton(resourceManager, "X");
        leaderboardListView = new ListView<>();
        statusLabel         = new Label("Loading leaderboard…");
        sortDropdown = new ComboBox<>();
        sortDropdown.getItems().addAll(
                "Win Rate",
                "Wins",
                "Losses",
                "Games Played",
                "Total Play Time",
                "Avg Moves / Game",
                "Avg Sec / Move",
                "Total Score"
        );
        sortDropdown.setValue("Win Rate");
    }

    private void layoutNodes() {

        returnButton.setFont(resourceManager.getFont(Fonts.PRESSSTART2BUTTONSTYLE));

        Text title = new Text("Leaderboard\n");
        title.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        title.setFill(Color.web(resourceManager.getTheme().getTextColor()));

        Label sortLabel = new Label("Sort by: ");
        sortLabel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));
        sortLabel.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        sortDropdown.setStyle("-fx-font-size: 11px;");

        HBox sortBox = new HBox(10, sortLabel, sortDropdown);
        sortBox.setAlignment(Pos.CENTER_LEFT);
        sortBox.setPadding(new Insets(0, 0, 8, 0));

        HBox titleBox = new HBox(title);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(30, 0, 10, 0));

        VBox topBox = new VBox(4, titleBox, sortBox);


        statusLabel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));
        statusLabel.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        HBox statusBox = new HBox(statusLabel);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setPadding(new Insets(8, 0, 20, 0));


        leaderboardListView.setPrefWidth(700);
        leaderboardListView.setPrefHeight(500);
        leaderboardListView.setStyle(
                "-fx-background-color: transparent; -fx-border-color: transparent;"
        );


        leaderboardListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                    return;
                }
                Label cellLabel = new Label(item);
                cellLabel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));
                cellLabel.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
                setGraphic(cellLabel);
                setText(null);
            }
        });


        BorderPane innerPane = new BorderPane();
        innerPane.setTop(topBox);
        innerPane.setCenter(leaderboardListView);
        innerPane.setBottom(statusBox);
        innerPane.setPadding(new Insets(40, 110, 30, 110));


        StackPane stackPane = new StackPane(innerPane, returnButton);

        StackPane.setAlignment(innerPane, Pos.CENTER);
        StackPane.setMargin(innerPane, new Insets(40, 0, 40, 0));

        StackPane.setAlignment(returnButton, Pos.TOP_LEFT);
        StackPane.setMargin(returnButton, new Insets(30, 0, 0, 650));


        Image backgroundImage = resourceManager.getImage(Components.PEGVIEW);
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );
        stackPane.setBackground(new Background(bgImage));

        setCenter(stackPane);
        setBackground(Background.EMPTY);
        setMaxSize(800, 800);
        setMinSize(800, 800);
    }


    public void setItems(List<String> rows) {
        leaderboardListView.getItems().setAll(rows);
    }


    public void setStatusText(String text) {
        statusLabel.setText(text);
    }


    public Button getReturnButton() {
        return returnButton;
    }

    public ComboBox<String> getSortDropdown() {
        return sortDropdown;
    }
}
