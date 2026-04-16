package game.integration_project1_zaroc.view.pages.multiplayerhostview;

import game.integration_project1_zaroc.dao.PlayersDao;
import game.integration_project1_zaroc.dao.RoomDTO;
import game.integration_project1_zaroc.dao.ZarocDaoException;
import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.model.gameinfo.PawnColor;
import game.integration_project1_zaroc.model.players.Player;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.pawncolors.PawnColorPaths;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.profilePictures.ProfilePictures;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MultiPlayerHostPresenter {

    private MultiPlayerHostView view;
    private AppController model;
    private PlayersDao playersDao;
    private String currentRoomCode;
    private RoomDTO currentRoomData;

    public MultiPlayerHostPresenter(MultiPlayerHostView view, AppController model) {
        this.view = view;
        this.model = model;
        this.playersDao = new PlayersDao();
        updateView();
        initLobby();
        addEventHandlers();
    }

    private void addEventHandlers(){
        view.getHostColorPicker().getRightButton().setOnAction(event -> {
            model.getColorOne().increaseCurrentIndex();
            if (currentRoomData != null && currentRoomData.getGuestId() != 0) {
                if (PawnColor.values()[model.getColorOne().getCurrentIndex()] == currentRoomData.getGuestColor()){
                    model.getColorOne().decreaseCurrentIndex();
                }
            }

            updateView();
            updateHostColor();

        });

        view.getHostColorPicker().getLeftButton().setOnAction(event -> {
            model.getColorOne().decreaseCurrentIndex();
            if (currentRoomData != null && currentRoomData.getGuestId() != 0) {
                if (PawnColor.values()[model.getColorOne().getCurrentIndex()] == currentRoomData.getGuestColor()){
                    model.getColorOne().decreaseCurrentIndex();
                }
            }

            updateView();
            updateHostColor();
        });

        view.getStartGameButton().setOnAction(e -> {
            try {
                System.out.println("Host klikt op START. GameID aanmaken...");
                int gameId = model.getMultiplayerService().getRoomDao().startGame(currentRoomData.getRoomId());

                model.getMultiplayerService().stopPolling();

                Player guest = playersDao.getPlayerById(currentRoomData.getGuestId());

                PawnColor gekozenHostKleur = PawnColor.values()[model.getColorOne().getCurrentIndex()];
                PawnColor guestKleur = currentRoomData.getGuestColor();

                model.initOnlineGame(gameId, guest, gekozenHostKleur, guestKleur, true);
                closeWindow();

            } catch (ZarocDaoException ex) {
                System.err.println("Starten mislukt: " + ex.getMessage());
                view.getStatusLabel().setText("START FAILED");
            }
        });

        view.getReturnButton().setOnAction(e -> {
            model.getMultiplayerService().stopPolling();
            model.setOnlineMultiplayer(false);
            model.setHost(false);
            model.setPlayer2(null);

            try{
                model.getMultiplayerService().getRoomDao().updateGameStatus("FINISHED",currentRoomCode);
            } catch (ZarocDaoException ex) {
                showAlert(ex.toString());
            }

            closeWindow();
        });
    }

    private void updateView(){
        view.getHostColorPicker().getImageView().setImage(view.getResourceManager().getPawnColor(PawnColorPaths.values()[model.getColorOne().getCurrentIndex()]));
        view.getHostPfpView().setImage(view.getResourceManager().getProfilePicture(ProfilePictures.valueOf(model.getPlayer1().getProfilePicture())));
        view.getHostName().setText(model.getPlayer1().getUsername());
    }

    private void updateHostColor(){
        try{
            model.getMultiplayerService().getRoomDao().setHostColor(currentRoomCode,model.getPlayer1().getPlayerId(),PawnColor.values()[model.getColorOne().getCurrentIndex()]);
        } catch (ZarocDaoException e) {
            showAlert(e.toString());
        }
    }

    private void initLobby() {
        view.getStartGameButton().setDisable(true);
        view.getStatusLabel().setText("Waiting on opponent...");

        currentRoomCode = model.getMultiplayerService().generateRoomCode();
        view.getRoomCodeDisplayLabel().setText("CODE: " + currentRoomCode);

        try {
            PawnColor startKleur = PawnColor.values()[model.getColorOne().getCurrentIndex()];
            model.getMultiplayerService().getRoomDao().createRoom(currentRoomCode, model.getPlayer1().getPlayerId(), startKleur);

            model.getMultiplayerService().startLobbyPolling(currentRoomCode, fetchedData -> {
                Platform.runLater(() -> processRoomData(fetchedData));
            });

        } catch (Exception e) {
            System.err.println("problem with creating: " + e.getMessage());
            view.getStatusLabel().setText("couldn't make room");
        }
    }

    private void processRoomData(RoomDTO fetchedData) {
        this.currentRoomData = fetchedData;

        if (currentRoomData.getGuestId() != 0) {
            if (view.getStartGameButton().isDisable()) {
                view.getStartGameButton().setDisable(false);
                view.getStatusLabel().setText("Opponent found!");

                try {
                    Player guest = playersDao.getPlayerById(currentRoomData.getGuestId());
                    if (guest != null) {
                        view.getGuestName().setText(guest.getUsername());
                        view.getGuestPfpView().setImage(view.getResourceManager().getProfilePicture(ProfilePictures.valueOf(guest.getProfilePicture())));
                    }
                } catch (Exception e) {
                    System.err.println("Couldn't load guest: " + e.getMessage());
                }
            }

            if (currentRoomData.getGuestColor() != null) {
                view.getGuestColor().setImage(view.getResourceManager().getPawnColor(PawnColorPaths.valueOf(currentRoomData.getGuestColor().name())));
            }

        } else {
            if (!view.getStartGameButton().isDisable()) {
                view.getStartGameButton().setDisable(true);
                view.getStatusLabel().setText("Waiting on opponent...");
                view.getGuestName().setText("Waiting...");
                view.getGuestPfpView().setImage(null);
            }
        }
    }


    private void closeWindow(){
        Stage stage = (Stage) view.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}