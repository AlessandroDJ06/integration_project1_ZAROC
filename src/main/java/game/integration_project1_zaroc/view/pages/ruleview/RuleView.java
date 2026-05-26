package game.integration_project1_zaroc.view.pages.ruleview;

import game.integration_project1_zaroc.view.components.buttons.TextButton;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class RuleView extends BorderPane {

    private TextButton returnButton;
    private final ResourceManager resourceManager;

    public RuleView(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        returnButton = new TextButton(resourceManager, "X");
    }

    void layoutNodes() {
        this.getChildren().clear();
        returnButton.updateLayout();
        this.setPrefSize(700, 700);
        this.setMaxSize(700, 700);

        Color textColor = Color.web(resourceManager.getTheme().getTextColor());
        Font titleFont = resourceManager.getFont(Fonts.PRESSSTART2PTITLE);
        Font largeFont = resourceManager.getFont(Fonts.PRESSSTART2PLARGE);
        Font smallFont = resourceManager.getFont(Fonts.PRESSSTART2PSMALL);

        returnButton.setFont(resourceManager.getFont(Fonts.PRESSSTART2BUTTONSTYLE));

        Text title = new Text("Rules");
        title.setFont(titleFont);
        title.setFill(textColor);

        StackPane header = new StackPane();
        header.getChildren().addAll(title, returnButton);
        StackPane.setAlignment(title, Pos.CENTER);
        StackPane.setAlignment(returnButton, Pos.CENTER_RIGHT);

        header.setPadding(new Insets(50, 70, 20, 70));

        Text hText1 = createText("Objective of the Game\n\n", largeFont, textColor);
        Text sText1 = createText("The objective of the game is to be the first to place three of your own pieces in the end row. The end row consists of the five spaces at the end of the board.\n\n\n", smallFont, textColor);

        Text hText2 = createText("Turn Sequence\n\n", largeFont, textColor);
        Text sText2 = createText("Players take turns. During your turn, you must perform two moves. These two moves may be made with any piece on the board, including your opponent’s pieces. You may also move the same piece twice, as long as the moves are valid.\n\n\n", smallFont, textColor);

        Text hText3 = createText("Movement\n\n", largeFont, textColor);
        Text sText3 = createText("""
        For each move, you take the top piece from a peg and move it sideways or forward.
        
        In a sideways move, you move a piece to an adjacent peg of the same height, provided there is still space on that peg.
        
        In a forward move, you move a piece to a peg of a lower height or to a space in the end row, following the forward connections of the board. A piece may only move forward when it is in the highest possible position on its peg, meaning there are no empty spaces above the piece.
        
        Pieces may never move backward to a higher peg.
        
        A move may not immediately undo the previous move.
        
        Pieces that reach the end row may no longer move, and sideways movement is not allowed in the end row.
        
        
        """, smallFont, textColor);

        Text hText4 = createText("Winning the Game\n\n", largeFont, textColor);
        Text sText4 = createText("The game ends immediately when a player has three pieces of their color in the end row. That player wins the game.", smallFont, textColor);
        TextFlow rules = new TextFlow(hText1, sText1, hText2, sText2, hText3, sText3, hText4, sText4);
        rules.setLineSpacing(8);

        rules.setPadding(new Insets(10, 80, 60, 80));

        ScrollPane scrollPane = new ScrollPane(rules);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-padding: 0;");
        BorderPane.setMargin(scrollPane, new Insets(0, 30, 60, 30));

        this.setTop(header);
        this.setCenter(scrollPane);

        Image boardBackgroundImage = resourceManager.getImage(Components.PEGVIEW);
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(
                boardBackgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );
        this.setBackground(new Background(backgroundImage));
    }

    private Text createText(String content, Font font, Color color) {
        Text text = new Text(content);
        text.setFont(font);
        text.setFill(color);
        return text;
    }

    public Button getReturnButton() {
        return returnButton;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }
}