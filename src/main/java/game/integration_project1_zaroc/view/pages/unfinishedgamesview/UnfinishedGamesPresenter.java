package game.integration_project1_zaroc.view.pages.unfinishedgamesview;

import game.integration_project1_zaroc.dao.UnfinishedGame;
import game.integration_project1_zaroc.dao.UnfinishedGamesDao;
import game.integration_project1_zaroc.dao.ZarocDaoException;
import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.model.players.AIPlayer;
import game.integration_project1_zaroc.model.players.HumanPlayer;
import game.integration_project1_zaroc.model.players.Player;
import game.integration_project1_zaroc.utils.Observer;
import game.integration_project1_zaroc.view.components.GameListCell;
import game.integration_project1_zaroc.view.pages.boardview.GameBoardPresenter;
import game.integration_project1_zaroc.view.pages.boardview.GameBoardView;
import game.integration_project1_zaroc.view.pages.gamesetupview.GameSetupPresenter;
import game.integration_project1_zaroc.view.pages.gamesetupview.GameSetupView;
import game.integration_project1_zaroc.view.pages.multiplayerhostview.MultiPlayerHostPresenter;
import game.integration_project1_zaroc.view.pages.multiplayerhostview.MultiPlayerHostView;
import game.integration_project1_zaroc.view.pages.playervsplayerview.PlayerVsPlayerPresenter;
import game.integration_project1_zaroc.view.pages.playervsplayerview.PlayerVsPlayerView;
import game.integration_project1_zaroc.view.pages.unfinishedgameplayervplayer.UnfinishedGamePlayerVsPlayerSetupPresenter;
import game.integration_project1_zaroc.view.pages.unfinishedgameplayervplayer.UnfinishedGamePlayerVsPlayerSetupView;
import game.integration_project1_zaroc.view.sharedlogic.NavigationService;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;
import java.util.Objects;

public class UnfinishedGamesPresenter implements Observer {
    private UnfinishedGamesView view;
    private AppController model;
    private UnfinishedGamesDao unfinishedGamesDao;
    private boolean continueMultiplayer;
    private Player playerTwo;
    private int selectedGameId;

    public UnfinishedGamesPresenter(UnfinishedGamesView view,AppController model){
        this.model = model;
        this.view = view;
        this.unfinishedGamesDao = new UnfinishedGamesDao();
        this.continueMultiplayer = false;
        view.getResourceManager().addObserver(this);
        this.playerTwo = null;
        this.selectedGameId = -1;
        addEventHandlers();
    }

    private void addEventHandlers(){
        view.getReturnButton().setOnAction(event -> {
            NavigationService.closeWindow(this.view);
        });
        GeneralEventhandlers.addHoverEffect(view.getReturnButton());


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
                    selectedGameId = selected.getGameId();

                    if(!selected.isOppIsAi()){
                        UnfinishedGamePlayerVsPlayerSetupView unfinishedGamePlayerVsPlayerSetupView = new UnfinishedGamePlayerVsPlayerSetupView(view.getResourceManager());
                        new UnfinishedGamePlayerVsPlayerSetupPresenter(unfinishedGamePlayerVsPlayerSetupView,model,selected);

                        Scene playerVsPlayerScene = new Scene(unfinishedGamePlayerVsPlayerSetupView);
                        playerVsPlayerScene.setFill(Color.TRANSPARENT);

                        Stage playerVsPlayerStage = new Stage();
                        playerVsPlayerStage.setScene(playerVsPlayerScene);
                        playerVsPlayerStage.setTitle("Speler vs Speler");
                        playerVsPlayerStage.initStyle(StageStyle.TRANSPARENT);
                        playerVsPlayerStage.initModality(Modality.APPLICATION_MODAL);
                        playerVsPlayerStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png"))));
                        playerVsPlayerStage.setResizable(false);

                        playerVsPlayerStage.showAndWait();
                        if (model.isOnlineMultiplayer()){
                            model.setContinueInMultiplayer(true);
                            MultiPlayerHostView multiPlayerHostView = new MultiPlayerHostView(view.getResourceManager());
                            new MultiPlayerHostPresenter(multiPlayerHostView, model,selectedGameId);
                            Scene hostScene = new Scene(multiPlayerHostView, 900, 750);
                            hostScene.setFill(Color.TRANSPARENT);
                            Stage hostStage = new Stage();
                            hostStage.setScene(hostScene);
                            hostStage.setTitle("Host Game");
                            hostStage.initStyle(StageStyle.TRANSPARENT);
                            hostStage.initModality(Modality.APPLICATION_MODAL);
                            hostStage.getIcons().add(new Image(Objects.requireNonNull(
                                    getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png")
                            )));

                            hostStage.setResizable(false);
                            hostStage.showAndWait();
                            checkIfGameIsEmpty();

                        }else if (model.getPlayer2() != null){
                            playerTwo = model.getPlayer2();
                            model.setColorPlayerTwo(selected.getOpponentPlayerColor());
                            model.setColorPlayerOne(selected.getCurrentPlayerColor());
                        }

                    }else{
                        playerTwo = new AIPlayer(selected.getOppDifficulty(),selected.getOpponentName());
                    }

                    if (!model.isOnlineMultiplayer()){
                        if (model.getPlayer2() != null){
                            playerTwo.setPlayerId(selected.getOppId());
                            playerTwo.setProfilePicture(selected.getOpponentPfp());
                            model.setPlayer2(playerTwo);
                        }

                        if (model.getPlayer1() == null){
                            model.setPlayer1(playerOne);
                        }

                        model.resumeGame(selectedGameId,model.getPlayer1().getUsername().equals(selected.getCurrentUserName()));
                        closeWindow();
                    }

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

    private void checkIfGameIsEmpty() {
        if (model.getGame() != null){
            closeWindow();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) view.getScene().getWindow();
        stage.close();
    }

    public int getSelectedGameId() {
        return selectedGameId;
    }

    @Override
    public void updateLayout(Object args) {
        view.layoutNodes();
    }
}
