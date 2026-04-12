package game.integration_project1_zaroc.view.pages.unfinishedgamesview;

import game.integration_project1_zaroc.dao.UnfinishedGame;
import game.integration_project1_zaroc.dao.UnfinishedGamesDao;
import game.integration_project1_zaroc.dao.ZarocDaoException;
import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.model.players.AIPlayer;
import game.integration_project1_zaroc.model.players.HumanPlayer;
import game.integration_project1_zaroc.model.players.Player;
import game.integration_project1_zaroc.view.components.GameListCell;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
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

        view.getUnfinishedGames().setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                UnfinishedGame selected = view.getUnfinishedGames().getSelectionModel().getSelectedItem();
                if (selected != null) {
                    model.setColorPlayerOne(selected.getCurrentPlayerColor());
                    model.setColorPlayerTwo(selected.getOpponentPlayerColor());
                    Player playerOne = (selected.isCurrIsAi() ?
                            new AIPlayer(selected.getCurrDifficulty(), selected.getCurrentUserName())
                            : new HumanPlayer(selected.getCurrentUserName(),selected.getCurrEmail()));
                    playerOne.setPlayerId(selected.getCurrId());
                    playerOne.setProfilePicture(selected.getCurrentPlayerPfp());
                    Player playerTwo = (selected.isOppIsAi() ?
                            new AIPlayer(selected.getOppDifficulty(),selected.getOpponentName()) :
                            new HumanPlayer(selected.getOpponentName(),selected.getOppEmail()));
                    playerTwo.setPlayerId(selected.getOppId());
                    playerTwo.setProfilePicture(selected.getOpponentPfp());

                    if (model.getPlayer1() == null){
                        model.setPlayer1(playerOne);
                    }
                    model.setPlayer2(playerTwo);

                    model.resumeGame(selected.getGameId());
                    view.getScene().getWindow().hide();
                }
            }
        });
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
