package game.integration_project1_zaroc.view.pages.ruleview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.stage.Stage;

public class RuleViewPresenter {
private final RuleView view;
    private final AppController model;

    public RuleViewPresenter(RuleView ruleView, AppController model) {
        this.view = ruleView;
        this.model = model;
        addEventHandlers();

    }

    private void addEventHandlers(){
        view.getReturnButton().setOnAction(actionEvent ->{
            Stage stage = (Stage) view.getScene().getWindow();
            stage.close();
        });
        GeneralEventhandlers.addHoverEffect(view.getReturnButton());
    }
}
