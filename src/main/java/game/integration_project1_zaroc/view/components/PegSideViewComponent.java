package game.integration_project1_zaroc.view.components;

import game.integration_project1_zaroc.view.core.ResourceManager;
import game.integration_project1_zaroc.view.core.themes.Components;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.ArrayList;

public class PegSideViewComponent extends BorderPane {
    private final ResourceManager resourceManager;
    private HBox pegRowFour;
    private HBox pegRowTwo;
    private HBox pegRowThree;
    private VBox rows;

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
    }

    private void layoutNodes(){
        Image poleCapaFour = resourceManager.getImage(Components.POLECAPAFOUR);
        Image poleCapaThree = resourceManager.getImage(Components.POLECAPATHREE);
        Image poleCapaTwo = resourceManager.getImage(Components.POLECAPATWO);
        ArrayList<ImageView> backgroundOfEachPeg = new ArrayList<>();

        for (int i = 0 ; i < 13 ; i++){
            if (i < 4){
                backgroundOfEachPeg.add(new ImageView(poleCapaFour));
            } else if (i < 8) {
                backgroundOfEachPeg.add(new ImageView(poleCapaThree));
            } else {
                backgroundOfEachPeg.add(new ImageView(poleCapaTwo));
            }

        }

        this.pegRowFour.getChildren().addAll(
                backgroundOfEachPeg.get(0),
                backgroundOfEachPeg.get(1),
                backgroundOfEachPeg.get(2),
                backgroundOfEachPeg.get(3)
        );

        this.pegRowThree.getChildren().addAll(
                backgroundOfEachPeg.get(4),
                backgroundOfEachPeg.get(5),
                backgroundOfEachPeg.get(6),
                backgroundOfEachPeg.get(7)
        );

        this.pegRowTwo.getChildren().addAll(
                backgroundOfEachPeg.get(8),
                backgroundOfEachPeg.get(9),
                backgroundOfEachPeg.get(10),
                backgroundOfEachPeg.get(11),
                backgroundOfEachPeg.get(12)
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
                ((HBox) row).setSpacing(50);
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
}
