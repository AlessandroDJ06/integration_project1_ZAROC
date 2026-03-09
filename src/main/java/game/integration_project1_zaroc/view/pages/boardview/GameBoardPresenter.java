package game.integration_project1_zaroc.view.pages.boardview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.view.pages.ruleview.RuleView;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.util.Arrays;
import java.util.List;

public class GameBoardPresenter {
    private GameBoardView view;
    private AppController model;
    private List<HBox> rows;
    private List<Button> buttons;

    public GameBoardPresenter(GameBoardView view,AppController model){
        this.view = view;
        this.model = model;
        this.rows = Arrays.asList(view.getPegRowFour(),view.getPegRowThree(),view.getPegRowTwo());
        this.buttons = Arrays.asList(view.getUndoButton(),view.getSettingsButton(),view.getInfoButton());
        updateView();
        addEventHandlers();
    }


    private void addEventHandlers(){
        view.getInfoButton().setOnAction(event -> {
            RuleView ruleView = new RuleView(view.getResourceManager());
            view.getScene().setRoot(ruleView);
        });

        for (Button button : buttons){
            button.setOnMouseEntered(event -> {
                button.setScaleX(1.2);
                button.setScaleY(1.2);
            });

            button.setOnMouseExited(event -> {
                button.setScaleX(1.0);
                button.setScaleY(1.0);
            });
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
                row.setSpacing(50);
            });
        }
    }

    private void updateView(){

    }


}
