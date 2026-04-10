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

    private void layoutNodes() {
        this.setPrefSize(700, 700);
        this.setMaxSize(700, 700);

        Color textColor = Color.web(resourceManager.getTheme().getTextColor());
        Font titleFont = resourceManager.getFont(Fonts.PRESSSTART2PTITLE);
        Font largeFont = resourceManager.getFont(Fonts.PRESSSTART2PLARGE);
        Font smallFont = resourceManager.getFont(Fonts.PRESSSTART2PSMALL);

        returnButton.setFont(resourceManager.getFont(Fonts.PRESSSTART2BUTTONSTYLE));

        Text title = new Text("Regels");
        title.setFont(titleFont);
        title.setFill(textColor);

        StackPane header = new StackPane();
        header.getChildren().addAll(title, returnButton);
        StackPane.setAlignment(title, Pos.CENTER);
        StackPane.setAlignment(returnButton, Pos.CENTER_RIGHT);

        header.setPadding(new Insets(50, 70, 20, 70));

        Text hText1 = createText("Doel van het spel\n\n", largeFont, textColor);
        Text sText1 = createText("Het doel van het spel is om als eerste drie van je eigen stukken in de eindrij te plaatsen. De eindrij bestaat uit de vijf vakjes aan het einde van het bord.\n\n\n", smallFont, textColor);

        Text hText2 = createText("Beurtverloop\n\n", largeFont, textColor);
        Text sText2 = createText("Spelers spelen om de beurt. Tijdens je beurt moet je twee zetten uitvoeren. Deze twee zetten mogen met eender welk stuk op het bord gebeuren, ook met stukken van je tegenstander. Je mag ook twee keer hetzelfde stuk verplaatsen, zolang de zetten geldig zijn.\n\n\n", smallFont, textColor);

        Text hText3 = createText("Beweging\n\n", largeFont, textColor);
        Text sText3 = createText("""
                Bij elke zet neem je het bovenste stuk van een pin en verplaats je het zijwaarts of vooruit.
                
                Bij een zijwaartse zet verplaats je een stuk naar een aangrenzende pin van dezelfde hoogte, op voorwaarde dat er nog plaats is op die pin.
                
                Bij een voorwaartse zet verplaats je een stuk naar een pin van een lagere hoogte of naar een vakje in de eindrij, volgens de voorwaartse verbindingen van het bord. Een stuk mag alleen vooruit bewegen wanneer het zich op de hoogst mogelijke positie van zijn pin bevindt, dus wanneer er geen lege plaatsen boven het stuk zijn.
                
                Stukken mogen nooit achteruit bewegen naar een hogere pin.
                
                Een zet mag de vorige zet niet onmiddellijk ongedaan maken.
                
                Stukken die de eindrij bereiken mogen niet meer bewegen, en zijwaartse beweging is in de eindrij niet toegestaan.
                
                
                """, smallFont, textColor);

        Text hText4 = createText("Het spel winnen\n\n", largeFont, textColor);
        Text sText4 = createText("Het spel eindigt onmiddellijk wanneer een speler drie stukken van zijn kleur in de eindrij heeft. Die speler wint het spel.", smallFont, textColor);

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
}