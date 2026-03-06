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
    }

    private void updateView(){

    }


}
