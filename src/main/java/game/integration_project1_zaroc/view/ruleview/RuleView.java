package game.integration_project1_zaroc.view.ruleview;

import game.integration_project1_zaroc.components.GeneralActionsComponent;
import game.integration_project1_zaroc.core.ResourceManager;
import game.integration_project1_zaroc.core.fonts.Fonts;
import game.integration_project1_zaroc.core.themes.Components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.layout.*;



public class RuleView extends BorderPane {

    private Label titel;

    private Label ruleLable1;
    private Label ruleLable2;
    private Label ruleLable3;
    private Label ruleLable4;
    private Label ruleLable5;
    private Label ruleLable6;
    private Label ruleLable7;
    private Label ruleLable8;
    private Label ruleLable9;
    private Label ruleLable10;
    private Label ruleLable11;
    private Label ruleLable12;
    private Label ruleLable13;
    private Label ruleLable14;
    private Label ruleLable15;
    private Label ruleLable16;
    private Label ruleLable17;
    private Label ruleLable18;


    private Button returnButton;
    private ResourceManager resourceManager;

    public RuleView(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    public void initialiseNodes() {
        titel = new Label();
        this.returnButton = new GeneralActionsComponent(this.resourceManager, Components.UNDO); //TODO: make a return button and add here
        ruleLable1 =new Label();
        ruleLable2 = new Label();
        ruleLable3 =new Label();
        ruleLable4 =new Label();
        ruleLable5 =new Label();
        ruleLable6 =new Label();
        ruleLable7 =new Label();
        ruleLable8 =new Label();
        ruleLable9 =new Label();
        ruleLable10 =new Label();
        ruleLable11 =new Label();
        ruleLable12 =new Label();
        ruleLable13 =new Label();
        ruleLable14 =new Label();
        ruleLable15 =new Label();
        ruleLable16 =new Label();
        ruleLable17 =new Label();
        ruleLable18 =new Label();
    }

    public void layoutNodes() {
        titel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        titel.setText("Rules: ");                      //zou een title lettertypen moeten hebben
        VBox rules = new VBox(titel,ruleLable1,ruleLable2,ruleLable3,ruleLable4,ruleLable5,ruleLable6,ruleLable7,ruleLable8,ruleLable9,ruleLable10,ruleLable11,ruleLable12,ruleLable13,ruleLable14,ruleLable15,ruleLable16,ruleLable17,ruleLable18);
        rules.setSpacing(10);

        ruleLable1.setText("Spelers");
        ruleLable1.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));

        ruleLable2.setText("Zaroc is een abstract strategiespel voor twee spelers. Elke speler speelt met stukken van één kleur.");
        ruleLable2.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));

        ruleLable3.setText("Doel van het spel");
        ruleLable3.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));

        ruleLable4.setText("Het doel van het spel is om als eerste drie van je eigen stukken in de eindrij te plaatsen. De eindrij bestaat uit de vijf vakjes aan het einde\nvan het bord.");
        ruleLable4.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));

        ruleLable5.setText("Opstelling");
        ruleLable5.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));

        ruleLable6.setText("Het bord bestaat uit 13 pinnen (palen) met afnemende hoogte, die naar de eindrij leiden. Aan het begin van het spel worden de vier hoogste\npinnen gevuld met telkens vier stukken, geplaatst in afwisselende kleuren. Elke pin vormt een stapel,\nen alleen het bovenste stuk van een stapel mag verplaatst worden.");
        ruleLable6.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));

        ruleLable7.setText("Beurtverloop");
        ruleLable7.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));

        ruleLable8.setText("Spelers spelen om de beurt. Tijdens je beurt moet je twee zetten uitvoeren. Deze twee zetten mogen met eender welk stuk op het bord gebeuren,\nook met stukken van je tegenstander. Je mag ook twee keer hetzelfde stuk verplaatsen, zolang de zetten geldig zijn.");
        ruleLable8.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));

        ruleLable9.setText("Beweging");
        ruleLable9.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));

        ruleLable10.setText("Bij elke zet neem je het bovenste stuk van een pin en verplaats je het zijwaarts of vooruit.");
        ruleLable10.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));

        ruleLable11.setText("Zijwaartse beweging");
        ruleLable11.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));             //zou een tussenin lettertype moeten hebben

        ruleLable12.setText("Bij een zijwaartse zet verplaats je een stuk naar een aangrenzende pin van dezelfde hoogte, op voorwaarde dat er nog plaats is op die pin.");
        ruleLable12.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));

        ruleLable13.setText("Voorwaartse beweging");                                       // tussenin lettertype
        ruleLable13.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));

        ruleLable14.setText("Bij een voorwaartse zet verplaats je een stuk naar een pin van een lagere hoogte of naar een vakje in de eindrij, volgens de voorwaartse\nverbindingen van het bord. Een stuk mag alleen vooruit bewegen wanneer het zich op de hoogst mogelijke positie van zijn pin bevindt,\ndus wanneer er geen lege plaatsen boven het stuk zijn.");
        ruleLable14.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));

        ruleLable15.setText("Beperkingen");
        ruleLable15.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));

        ruleLable16.setText("Stukken mogen nooit achteruit bewegen naar een hogere pin. Een zet mag de vorige zet niet onmiddellijk ongedaan maken. Dat betekent dat het\nlaatst verplaatste stuk niet meteen terug naar zijn vorige plaats mag worden verplaatst, zelfs niet door de andere speler.\nStukken die de eindrij bereiken mogen niet meer bewegen, en zijwaartse beweging is in de eindrij niet toegestaan.");
        ruleLable16.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));

        ruleLable17.setText("Het spel winnen");
        ruleLable17.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));

        ruleLable18.setText("Het spel eindigt onmiddellijk wanneer een speler drie stukken van zijn kleur in de eindrij heeft. Die speler wint het spel.");
        ruleLable18.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));


        setCenter(rules);
        BorderPane.setAlignment(rules, Pos.CENTER);
        BorderPane.setMargin(rules, new Insets(150,0,0,200));

        setTop(returnButton);
        BorderPane.setAlignment(returnButton, Pos.TOP_LEFT);


        this.setStyle("-fx-background-color: " + this.resourceManager.getTheme().getColor() + ";");

    }
}
