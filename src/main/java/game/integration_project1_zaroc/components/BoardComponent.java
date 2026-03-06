package game.integration_project1_zaroc.components;
import game.integration_project1_zaroc.core.ResourceManager;
import game.integration_project1_zaroc.core.themes.Components;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class BoardComponent extends StackPane {
    private GridPane board;
    private final ResourceManager resourceManager;

    public BoardComponent(ResourceManager resourceManager){
        this.resourceManager = resourceManager;
        initialiseComponent();
        layoutComponent();
    }

    public void initialiseComponent(){
        this.board = new GridPane();
        this.board.setHgap(5);
        this.board.setVgap(5);
    }

    public void layoutComponent(){
        //bepaald hoe groot het board getoond wordt op de view
        setPrefSize(600, 400);
        setMinSize(600, 400);
        setMaxSize(600, 400);
        setAlignment(Pos.CENTER); //zorg dat alles centraal is uitgelijnd

        //achtergrond van het board
        Image boardBackgroundImage = resourceManager.getImage(Components.BOARD);
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(
                boardBackgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );
        this.board.setBackground(new Background(backgroundImage));

        //grootte van het bord zelf bepalen
        board.setPrefSize(750,550);
        board.setMinSize(750,550);
        board.setMaxSize(750,550);
        board.setPadding(new Insets(70,0,10,70)); //TopRightBottomLeft

        //voeg peg locaties toe
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 10; column++) {
                boolean shouldAdd = (row < 2 && column % 2 != 0 && column != 9)
                        || (row >= 2 && column % 2 == 0);

                if (shouldAdd) {
                    Components path;
                    if (row == 3){
                        path = Components.FINISH;
                    } else {
                        path = Components.PEG;
                    }
                    ImageView location = new ImageView(resourceManager.getImage(path));
                    location.setScaleY(1.3);
                    location.setScaleX(1.3);
                    board.add(location, column, row);

                    GridPane.setHalignment(location, HPos.CENTER);
                    GridPane.setValignment(location, VPos.CENTER);
                }
            }
        }

        //constraint voor gelijke verdeling tussen kolommen en rijen
        for (int i = 0; i < 10; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(10); // 10 kolommen → 10% breed per kolom
            board.getColumnConstraints().add(col);
        }

        for (int i = 0; i < 4; i++) {
            RowConstraints rowC = new RowConstraints();
            rowC.setPercentHeight(23); // 4 rijen → 25% hoogte per rij
            board.getRowConstraints().add(rowC);
        }

        //voeg het bord toe aan de pane
        getChildren().add(board);
    }

}
