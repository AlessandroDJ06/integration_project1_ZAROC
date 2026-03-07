package game.integration_project1_zaroc.view.boardview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.view.ruleview.RuleView;
import javafx.scene.Cursor;

public class GameBoardPresenter {
    private GameBoardView view;
    private AppController model;

    public GameBoardPresenter(GameBoardView view,AppController model){
        this.view = view;
        this.model = model;
        updateView();
        addEventHandlers();
    }

    private void addEventHandlers(){
        view.getInfoButton().setOnAction(event -> {
            RuleView ruleView = new RuleView(view.getResourceManager());
            view.getScene().setRoot(ruleView);
        });

        view.getInfoButton().setOnMouseEntered(event -> {
            view.getInfoButton().setScaleX(1.2);
            view.getInfoButton().setScaleY(1.2);
        });

        view.getInfoButton().setOnMouseExited(event -> {
            view.getInfoButton().setScaleX(1.0);
            view.getInfoButton().setScaleY(1.0);
        });

        view.getPegRowFour().setOnMouseEntered(mouseEvent -> {
            view.getPegRowFour().setScaleY(1.5);
            view.getPegRowFour().setScaleX(1.5);
            view.getPegRowFour().setSpacing(30);
        });

        view.getPegRowFour().setOnMouseExited(mouseEvent -> {
            view.getPegRowFour().setScaleY(1);
            view.getPegRowFour().setScaleX(1);
            view.getPegRowFour().setSpacing(50);
        });

        view.getPegRowThree().setOnMouseEntered(mouseEvent -> {
            view.getPegRowThree().setScaleY(1.5);
            view.getPegRowThree().setScaleX(1.5);
            view.getPegRowThree().setSpacing(30);
        });

        view.getPegRowThree().setOnMouseExited(mouseEvent -> {
            view.getPegRowThree().setScaleY(1);
            view.getPegRowThree().setScaleX(1);
            view.getPegRowThree().setSpacing(50);
        });

        view.getPegRowTwo().setOnMouseEntered(mouseEvent -> {
            view.getPegRowTwo().setScaleY(1.5);
            view.getPegRowTwo().setScaleX(1.5);
            view.getPegRowTwo().setSpacing(30);
        });

        view.getPegRowTwo().setOnMouseExited(mouseEvent -> {
            view.getPegRowTwo().setScaleY(1);
            view.getPegRowTwo().setScaleX(1);
            view.getPegRowTwo().setSpacing(50);
        });
    }

    private void updateView(){

    }


}
