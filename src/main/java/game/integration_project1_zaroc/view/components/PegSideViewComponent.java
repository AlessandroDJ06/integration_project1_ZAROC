package game.integration_project1_zaroc.view.components;

import game.integration_project1_zaroc.view.core.ResourceManager;
import game.integration_project1_zaroc.view.core.themes.Components;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class PegSideViewComponent extends BorderPane {
    private final ResourceManager resourceManager;
    private HBox pegRowFour;
    private HBox pegRowTwo;
    private HBox pegRowThree;
    private VBox rows;
    private List<VBox> pegContainers;

    public PegSideViewComponent(ResourceManager resourceManager){
        this.resourceManager = resourceManager;
        styleComponent();
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes(){
        this.pegRowFour = new HBox();
        this.pegRowThree = new HBox();
        this.pegRowTwo = new HBox();
        this.rows = new VBox();
        this.pegContainers = new ArrayList<>();
    }

    private void setBackgroundImage(Region region, Image image) {
        BackgroundImage bImg = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, false) // Zorgt dat het vult
        );
        region.setBackground(new Background(bImg));
    }

    private void layoutNodes(){
        Image poleCapaFour = resourceManager.getImage(Components.POLECAPAFOUR);
        Image poleCapaThree = resourceManager.getImage(Components.POLECAPATHREE);
        Image poleCapaTwo = resourceManager.getImage(Components.POLECAPATWO);

        for (int i = 0 ; i < 13 ; i++){
            VBox pegBox = new VBox();
            pegBox.setPrefSize(15,75);
            pegBox.setAlignment(Pos.BOTTOM_CENTER);
            Image currentImage;
            if (i < 4){
                currentImage = poleCapaFour;
            } else if (i < 8) {
                currentImage = poleCapaThree;
            } else {
                currentImage = poleCapaTwo;
            }
            setBackgroundImage(pegBox,currentImage);
            pegContainers.add(pegBox);
        }



        this.pegRowFour.getChildren().addAll(
                pegContainers.get(0),
                pegContainers.get(1),
                pegContainers.get(2),
                pegContainers.get(3)
        );

        this.pegRowThree.getChildren().addAll(
                pegContainers.get(4),
                pegContainers.get(5),
                pegContainers.get(6),
                pegContainers.get(7)
        );

        this.pegRowTwo.getChildren().addAll(
                pegContainers.get(8),
                pegContainers.get(9),
                pegContainers.get(10),
                pegContainers.get(11),
                pegContainers.get(12)
        );

        rows.getChildren().addAll(
                this.pegRowFour,
                this.pegRowThree,
                this.pegRowTwo
        );
        rows.setSpacing(20);
        rows.setAlignment(Pos.CENTER);

        for (Node row : rows.getChildren()){
            if (row instanceof HBox){
                ((HBox) row).setAlignment(Pos.CENTER);
                ((HBox) row).setSpacing(60);
            }
        }

        setCenter(rows);

    }

    public void styleComponent(){
        Image boardBackgroundImage = resourceManager.getImage(Components.PEGVIEW);
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(
                boardBackgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );

        setBackground(new Background(backgroundImage));
        setMaxSize(400,400);
        setPrefSize(400,400);
    }

    public HBox getPegRowThree() {
        return pegRowThree;
    }

    public HBox getPegRowTwo() {
        return pegRowTwo;
    }

    public HBox getPegRowFour() {
        return pegRowFour;
    }

    public List<VBox> getPegContainers() {
        return pegContainers;
    }
}
