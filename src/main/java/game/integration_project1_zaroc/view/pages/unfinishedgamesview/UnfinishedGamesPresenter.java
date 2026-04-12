package game.integration_project1_zaroc.view.pages.unfinishedgamesview;

import game.integration_project1_zaroc.dao.UnfinishedGame;
import game.integration_project1_zaroc.dao.UnfinishedGamesDao;
import game.integration_project1_zaroc.dao.ZarocDaoException;
import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.view.components.GameListCell;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.paint.Color;

import java.util.List;

public class UnfinishedGamesPresenter {
    private UnfinishedGamesView view;
    private AppController model;
    private UnfinishedGamesDao unfinishedGamesDao;

    public UnfinishedGamesPresenter(UnfinishedGamesView view,AppController model){
        this.model = model;
        this.view = view;
        this.unfinishedGamesDao = new UnfinishedGamesDao();
        addEventHandlers();
    }

    private void addEventHandlers(){
        getUnfinishedGames();
    }

    private void getUnfinishedGames(){
        if (model.isAllowedToUseDatabase()){
            try {

                List<UnfinishedGame> games = unfinishedGamesDao.fetchUnfinishedGames(model.getPlayer1().getPlayerId());
                ObservableList<UnfinishedGame> observableGames = FXCollections.observableArrayList(games);
                view.getUnfinishedGames().setItems(observableGames);
                view.getUnfinishedGames().setCellFactory(listView -> new GameListCell(view.getResourceManager()));

            } catch (ZarocDaoException e) {

                Label message = new Label("no database in use");
                message.setFont(view.getResourceManager().getFont(Fonts.PRESSSTART2PSMALL));
                message.setTextFill(Color.web(view.getResourceManager().getTheme().getTextColor()));
                view.getUnfinishedGames().setPlaceholder(message);

            }

        } else {

            Label message = new Label("playing as guest");
            message.setFont(view.getResourceManager().getFont(Fonts.PRESSSTART2PSMALL));
            message.setTextFill(Color.web(view.getResourceManager().getTheme().getTextColor()));
            view.getUnfinishedGames().setPlaceholder(message);
        }


    }


}
