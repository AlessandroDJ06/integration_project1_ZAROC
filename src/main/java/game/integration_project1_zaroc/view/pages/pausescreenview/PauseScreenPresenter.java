package game.integration_project1_zaroc.view.pages.pausescreenview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.model.gameinfo.GameStatus;
import game.integration_project1_zaroc.utils.Observer;
import game.integration_project1_zaroc.view.pages.boardview.GameBoardPresenter;
import game.integration_project1_zaroc.view.sharedlogic.NavigationService;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;

public class PauseScreenPresenter implements Observer {
    private PauseScreenView view;
    private AppController model;
    private boolean isContinued = false;
    private GameBoardPresenter gameBoardPresenter;


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
            if (gameBoardPresenter != null){ gameBoardPresenter.closeWarningPopup();}
            view.getResourceManager().getMusicManager().changeMusic();

            model.getGame().setStatus(GameStatus.PAUSED);
            model.setPlayer2(null);
            model.setGame(null);
            NavigationService.navigateToGameModeSelection(this.view.getResourceManager(),this.model);
            NavigationService.closeWindow(this.view);
        });
        GeneralEventhandlers.addHoverEffect(view.getReturnButton());

    }
    public void setGameBoardPresenter(GameBoardPresenter gameBoardPresenter) {
        this.gameBoardPresenter = gameBoardPresenter;
    }

    public boolean isContinued(){
        return isContinued;
    }
    @Override
    public void updateLayout(Object args) {
        view.layoutNodes();
    }
    }




