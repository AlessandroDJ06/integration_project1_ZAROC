package game.integration_project1_zaroc.view.pages.winwarningview;

import game.integration_project1_zaroc.model.AppController;
import javafx.stage.Stage;

public class WinWarningPresenter {
    private AppController model;
    private WinWarningView view;


    public WinWarningPresenter(AppController model, WinWarningView view) {
        this.model = model;
        this.view = view;
        addEventHandlers();
    }

    private void addEventHandlers() {
        view.getCloseButton().setOnAction(event -> {
            Stage currentStage = (Stage) view.getScene().getWindow();
            currentStage.close();
        });
    }
}
