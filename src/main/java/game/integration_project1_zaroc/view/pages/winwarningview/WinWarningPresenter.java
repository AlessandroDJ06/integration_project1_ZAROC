package game.integration_project1_zaroc.view.pages.winwarningview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.utils.Observer;
import javafx.stage.Stage;

public class WinWarningPresenter implements Observer {
    private AppController model;
    private WinWarningView view;


    public WinWarningPresenter(AppController model, WinWarningView view) {
        this.model = model;
        this.view = view;
        this.view.getResourceManager().addObserver(this);
        addEventHandlers();
    }

    private void addEventHandlers() {
        view.getCloseButton().setOnAction(event -> {
            Stage currentStage = (Stage) view.getScene().getWindow();
            currentStage.close();
        });
    }

    @Override
    public void updateLayout(Object args) {
        view.layoutNodes();
    }
}
