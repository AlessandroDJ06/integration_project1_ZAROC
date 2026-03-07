package game.integration_project1_zaroc.view.ruleview;


import game.integration_project1_zaroc.view.components.GeneralActionsComponent;
import game.integration_project1_zaroc.view.core.ResourceManager;

import game.integration_project1_zaroc.view.core.fonts.Fonts;
import game.integration_project1_zaroc.view.core.themes.Components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;


import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.List;


public class RuleView extends BorderPane {


    private BorderPane ruleBorderPane;
    private Button returnButton;
    private ResourceManager resourceManager;

    public RuleView(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    public void initialiseNodes() {
        ruleBorderPane = new BorderPane();
        this.returnButton = new GeneralActionsComponent(this.resourceManager, Components.UNDO); //TODO: make a return button and add here
    }

    public void layoutNodes() {


        //zou een title lettertypen moeten hebben
        Text titel = new Text("Regels:\n\n");
        Text hText1 = new Text("Doel van het spel\n\n");
        Text sText1 = new Text("Het doel van het spel is om als eerste drie van je eigen stukken in de eindrij te plaatsen. De eindrij bestaat uit de vijf vakjes aan het einde van het bord.\n\n");
        Text hText2 = new Text("Beurtverloop\n\n");
        Text sText2 = new Text("Spelers spelen om de beurt. Tijdens je beurt moet je twee zetten uitvoeren. Deze twee zetten mogen met eender welk stuk op het bord gebeuren, ook met stukken van je tegenstander. Je mag ook twee keer hetzelfde stuk verplaatsen, zolang de zetten geldig zijn.\n\n");
        Text hText3 = new Text("Beweging\n\n");
        Text sText3 = new Text("""
                Bij elke zet neem je het bovenste stuk van een pin en verplaats je het zijwaarts of vooruit.
                Bij een zijwaartse zet verplaats je een stuk naar een aangrenzende pin van dezelfde hoogte, op voorwaarde dat er nog plaats is op die pin.
                Bij een voorwaartse zet verplaats je een stuk naar een pin van een lagere hoogte of naar een vakje in de eindrij, volgens de voorwaartse verbindingen van het bord. Een stuk mag alleen vooruit bewegen wanneer het zich op de hoogst mogelijke positie van zijn pin bevindt, dus wanneer er geen lege plaatsen boven het stuk zijn.
                Stukken mogen nooit achteruit bewegen naar een hogere pin.
                Een zet mag de vorige zet niet onmiddellijk ongedaan maken. Dat betekent dat het laatst verplaatste stuk niet meteen terug naar zijn vorige plaats mag worden verplaatst, zelfs niet door de andere speler.
                Stukken die de eindrij bereiken mogen niet meer bewegen, en zijwaartse beweging is in de eindrij niet toegestaan.
                
                """);
        Text hText4 = new Text("Het spel winnen\n\n");
        Text sText4 = new Text("Het spel eindigt onmiddellijk wanneer een speler drie stukken van zijn kleur in de eindrij heeft. Die speler wint het spel.");

        List<Text> small = new ArrayList<>();
        small.add(sText1);
        small.add(sText2);
        small.add(sText3);
        small.add(sText4);

        List<Text> big = new ArrayList<>();
        big.add(titel);
        big.add(hText1);
        big.add(hText2);
        big.add(hText3);
        big.add(hText4);

        for (Text text : small) {
            text.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));
        }

        for (Text text : big) {
            text.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        }


        TextFlow rules = new TextFlow(titel, hText1, sText1, hText2, sText2, hText3, sText3, hText4, sText4);
        rules.setLineSpacing(5);


        Image boardBackgroundImage = resourceManager.getImage(Components.PEGVIEW);
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(
                boardBackgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );

        ruleBorderPane.setBackground(new Background(backgroundImage));
        ruleBorderPane.setMaxSize(1000, 800);
        ruleBorderPane.setPrefSize(1000, 800);
        ruleBorderPane.setCenter(rules);
        ruleBorderPane.setPadding(new Insets(50,150,50,150));

        setCenter(ruleBorderPane);
        BorderPane.setAlignment(ruleBorderPane, Pos.CENTER);
        BorderPane.setMargin(ruleBorderPane, new Insets(50,170 ,180 ,170 )); //top , right, bottom, left

        setTop(returnButton);
        BorderPane.setAlignment(returnButton, Pos.TOP_LEFT);


        this.setStyle("-fx-background-color: " + this.resourceManager.getTheme().getColor() + ";");

    }
}
