package game.integration_project1_zaroc.view.pages.settingsview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.view.pages.ruleview.RuleView;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.stage.Stage;

public class SettingsPresenter {

        private final SettingsView view;
        private final AppController model;

        public SettingsPresenter(SettingsView View, AppController model) {
            this.view = View;
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
