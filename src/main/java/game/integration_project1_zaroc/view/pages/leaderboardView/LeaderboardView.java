package game.integration_project1_zaroc.view.pages.leaderboardview;

import game.integration_project1_zaroc.dao.LeaderboardEntry; // Make sure to import this!
import game.integration_project1_zaroc.view.components.buttons.TextButton;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;

import javafx.beans.property.SimpleStringProperty;
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
    private TableView<LeaderboardEntry> leaderboardTable;
    private Label statusLabel;
    private ComboBox<String> sortDropdown;

    private final ResourceManager resourceManager;

    public LeaderboardView(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        returnButton = new TextButton(resourceManager, "X");
        leaderboardTable = new TableView<>();
        statusLabel = new Label("Loading leaderboard…");
        sortDropdown = new ComboBox<>();
        sortDropdown.getItems().addAll(
                "Win Rate", "Wins", "Losses", "Games Played",
                "Total Play Time", "Avg Moves / Game", "Avg Sec / Move", "Total Score"
        );
        sortDropdown.setValue("Win Rate");
    }

    private void layoutNodes() {
        this.setPrefWidth(1052);

        String themeColor = resourceManager.getTheme().getColor();
        String textColor = resourceManager.getTheme().getTextColor();
        String fontFamily = resourceManager.getFont(Fonts.PRESSSTART2PSMALL).getFamily();


        returnButton.setFont(resourceManager.getFont(Fonts.PRESSSTART2BUTTONSTYLE));

        Text title = new Text("Leaderboard");
        title.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        title.setFill(Color.web(textColor));

        StackPane header = new StackPane();
        header.getChildren().addAll(title, returnButton);
        StackPane.setAlignment(returnButton, Pos.CENTER_RIGHT);
        header.setPadding(new Insets(30, 40, 20, 40));

        Label sortLabel = new Label("Sort by: ");
        sortLabel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));
        sortLabel.setTextFill(Color.web(textColor));

        sortDropdown.setStyle(
                "-fx-background-color: derive(" + themeColor + ", 15%); " +
                        "-fx-border-color: derive(" + themeColor + ", -10%); " +
                        "-fx-border-width: 2px; " +
                        "-fx-font-size: 11px; " +
                        "-fx-text-base-color: " + textColor + "; " +
                        "-fx-text-fill: " + textColor + "; " +
                        "-fx-font-family: '" + fontFamily + "';"
        );

        sortDropdown.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: " + themeColor + ";");
                    return;
                }
                setText(item);
                setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));
                setTextFill(Color.web(textColor));

                if (getIndex() % 2 == 0) {
                    setStyle("-fx-background-color: derive(" + themeColor + ", 15%); -fx-padding: 8px;");
                } else {
                    setStyle("-fx-background-color: derive(" + themeColor + ", 30%); -fx-padding: 8px;");
                }
            }
        });

        sortDropdown.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    return;
                }
                setText(item);
                setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));
                setTextFill(Color.web(textColor));
                setStyle("-fx-background-color: transparent;");
            }
        });

        HBox sortBox = new HBox(10, sortLabel, sortDropdown);
        sortBox.setAlignment(Pos.CENTER_LEFT);
        sortBox.setPadding(new Insets(0, 0, 10, 0));

        TableColumn<LeaderboardEntry, String> rankCol = new TableColumn<>("Rank");
        rankCol.setCellValueFactory(data -> new SimpleStringProperty("#" + data.getValue().getRank()));
        rankCol.setPrefWidth(60);

        TableColumn<LeaderboardEntry, String> userCol = new TableColumn<>("Username");
        userCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));
        userCol.setPrefWidth(180);

        TableColumn<LeaderboardEntry, String> playedCol = new TableColumn<>("Plyd");
        playedCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getGamesPlayed())));

        TableColumn<LeaderboardEntry, String> winsCol = new TableColumn<>("Wins");
        winsCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getWins())));

        TableColumn<LeaderboardEntry, String> lossCol = new TableColumn<>("Loss");
        lossCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getLosses())));

        TableColumn<LeaderboardEntry, String> winPctCol = new TableColumn<>("Win %");
        winPctCol.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.1f%%", data.getValue().getWinPercentage())));

        TableColumn<LeaderboardEntry, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFormattedPlayTime()));
        timeCol.setPrefWidth(100);

        TableColumn<LeaderboardEntry, String> movesCol = new TableColumn<>("Moves");
        movesCol.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.1f", data.getValue().getAvgMovesPerGame())));

        TableColumn<LeaderboardEntry, String> secPerMoveCol = new TableColumn<>("S/Mov");
        secPerMoveCol.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.1f", data.getValue().getAvgSecPerMove())));

        TableColumn<LeaderboardEntry, String> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getTotalScore())));

        leaderboardTable.getColumns().addAll(
                rankCol, userCol, playedCol, winsCol, lossCol,
                winPctCol, timeCol, movesCol, secPerMoveCol, scoreCol
        );

        leaderboardTable.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-base: " + themeColor + "; " +
                        "-fx-control-inner-background: transparent; " +
                        "-fx-table-cell-border-color: transparent; " +
                        "-fx-font-family: '" + fontFamily + "'; " +
                        "-fx-font-size: 11px;"
        );

        leaderboardTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(LeaderboardEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("-fx-background-color: transparent;");
                } else {
                    if (getIndex() % 2 == 0) {
                        setStyle("-fx-background-color: derive(" + themeColor + ", -5%); -fx-text-background-color: " + textColor + ";");
                    } else {
                        setStyle("-fx-background-color: derive(" + themeColor + ", 10%); -fx-text-background-color: " + textColor + ";");
                    }
                }
            }
        });

        VBox contentBox = new VBox(10, sortBox, leaderboardTable);
        contentBox.setPadding(new Insets(20, 75, 20, 75));
        VBox.setVgrow(leaderboardTable, Priority.ALWAYS);

        statusLabel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));
        statusLabel.setTextFill(Color.web(textColor));

        HBox statusBox = new HBox(statusLabel);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setPadding(new Insets(10, 0, 30, 0));

        this.setTop(header);
        this.setCenter(contentBox);
        this.setBottom(statusBox);

        Image backgroundImage = resourceManager.getImage(Components.EXTRALARGECONTAINER);
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage bgImage = new BackgroundImage(
                backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, backgroundSize
        );
        this.setBackground(new Background(bgImage));
    }

    public void setItems(List<LeaderboardEntry> rows) {
        leaderboardTable.getItems().setAll(rows);
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

    public ResourceManager getResourceManager() {
        return resourceManager;
    }
}
