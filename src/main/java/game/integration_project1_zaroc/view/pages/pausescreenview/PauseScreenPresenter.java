package game.integration_project1_zaroc.view.pages.pausescreenview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.model.gameinfo.GameStatus;
import game.integration_project1_zaroc.view.pages.startview.StartPresenter;
import game.integration_project1_zaroc.view.pages.startview.StartView;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.stage.Stage;

public class PauseScreenPresenter {
    private PauseScreenView view;
    private AppController model;
    private boolean isContinued = false;

    public PauseScreenPresenter(PauseScreenView view, AppController model) {
        this.view = view;
        this.model = model;
        addEventHandlers();
    }

    private void addEventHandlers() {
        view.getContinueButton().setOnAction(event -> {
            isContinued = true;
            model.getGame().setStatus(GameStatus.PLAYING);
            closeWindow();
        });
        GeneralEventhandlers.addHoverEffect(view.getContinueButton());

        view.getReturnButton().setOnAction(event -> {
            model.getGame().setStatus(GameStatus.PAUSED);
            Stage menuStage = (Stage) ((Stage) view.getScene().getWindow()).getOwner();
            StartView startView = new StartView(view.getResourceManager());
            new StartPresenter(model, startView);
            menuStage.getScene().setRoot(startView);
            closeWindow();
        });
        GeneralEventhandlers.addHoverEffect(view.getReturnButton());

    }

    public void closeWindow(){
        Stage currentStage = (Stage) view.getScene().getWindow();
        currentStage.close();
    }

    public boolean isContinued(){
        return isContinued;
    }
    }


