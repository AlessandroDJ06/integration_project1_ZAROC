package game.integration_project1_zaroc.view.pages.boardview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.pawncolors.PawnSideViews;
import game.integration_project1_zaroc.view.pages.ruleview.RuleView;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
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
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.WHITE)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.BLACK)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.WHITE))
        );

        view.getPegContainers().get(1).getChildren().addAll(
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.WHITE)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.BLACK)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.WHITE)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.BLACK))

        );


        view.getPegContainers().get(2).getChildren().addAll(
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.BLACK)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.WHITE)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.BLACK)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.WHITE))
        );

        view.getPegContainers().get(3).getChildren().addAll(
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.WHITE)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.BLACK)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.WHITE))

        );

        view.getPegContainers().get(6).getChildren().add(new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.BLACK)));




    }


}
