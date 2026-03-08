package game.integration_project1_zaroc.view.boardview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.view.components.PegSideViewComponent;
import game.integration_project1_zaroc.view.core.pawncolors.PawnSideViews;
import game.integration_project1_zaroc.view.ruleview.RuleView;
import game.integration_project1_zaroc.view.utils.GeneralEventhandlers;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import java.util.Arrays;
import java.util.List;

public class GameBoardPresenter {
    private GameBoardView view;
    private AppController model;
    private GeneralEventhandlers generalEventhandlers;
    private List<HBox> rows;
    private List<Button> buttons;

    public GameBoardPresenter(GameBoardView view,AppController model){
        this.view = view;
        this.model = model;
        this.rows = Arrays.asList(view.getPegRowFour(),view.getPegRowThree(),view.getPegRowTwo());
        this.buttons = Arrays.asList(view.getUndoButton(),view.getSettingsButton(),view.getInfoButton());
        this.generalEventhandlers = new GeneralEventhandlers();
        updateView();
        addEventHandlers();
    }


    private void addEventHandlers(){
        view.getInfoButton().setOnAction(event -> {
            RuleView ruleView = new RuleView(view.getResourceManager());
            view.getScene().setRoot(ruleView);
        });

        for (Button button : buttons){
            generalEventhandlers.addHoverEffect(button);
        }

        for (HBox row : rows){
            row.setOnMouseEntered(mouseEvent -> {
                row.setScaleY(1.5);
                row.setScaleX(1.5);
                row.setSpacing(30);
            });

            row.setOnMouseExited(mouseEvent -> {
                row.setScaleY(1);
                row.setScaleX(1);
                row.setSpacing(60);
            });
        }
    }

    private void updateView(){
        view.getPegContainers().get(0).getChildren().addAll(
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.BLACK)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.DARKBLUE)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.BLACK)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.DARKBLUE))
        );

        view.getPegContainers().get(1).getChildren().addAll(
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.DARKBLUE)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.BLACK)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.DARKBLUE)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.BLACK))

        );


        view.getPegContainers().get(2).getChildren().addAll(
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.BLACK)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.DARKBLUE)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.BLACK)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.DARKBLUE))
        );

        view.getPegContainers().get(3).getChildren().addAll(
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.DARKBLUE)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.BLACK)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.DARKBLUE)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.BLACK))

        );




    }


}
