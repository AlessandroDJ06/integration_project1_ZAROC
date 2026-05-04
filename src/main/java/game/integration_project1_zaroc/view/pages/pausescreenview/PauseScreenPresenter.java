package game.integration_project1_zaroc.view.pages.pausescreenview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.model.gameinfo.GameStatus;
import game.integration_project1_zaroc.utils.Observer;
import game.integration_project1_zaroc.view.pages.startview.StartPresenter;
import game.integration_project1_zaroc.view.pages.startview.StartView;
import game.integration_project1_zaroc.view.sharedlogic.NavigationService;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.stage.Stage;

public class PauseScreenPresenter implements Observer {
    private PauseScreenView view;
    private AppController model;
    private boolean isContinued = false;

    public PauseScreenPresenter(PauseScreenView view, AppController model) {
        this.view = view;
        this.model = model;
        view.getResourceManager().addObserver(this);
        addEventHandlers();
    }

    private void addEventHandlers() {
        view.getContinueButton().setOnAction(event -> {
            isContinued = true;
            model.getGame().setStatus(GameStatus.PLAYING);
            NavigationService.closeWindow(this.view);
        });
        GeneralEventhandlers.addHoverEffect(view.getContinueButton());

        view.getReturnButton().setOnAction(event -> {
            model.getGame().setStatus(GameStatus.PAUSED);
            NavigationService.navigateToGameModeSelection(this.view.getResourceManager(),this.model,this.view);
            NavigationService.closeWindow(this.view);
        });
        GeneralEventhandlers.addHoverEffect(view.getReturnButton());

    }

    public boolean isContinued(){
        return isContinued;
    }
    @Override
    public void updateLayout(Object args) {
        view.layoutNodes();
    }
    }




