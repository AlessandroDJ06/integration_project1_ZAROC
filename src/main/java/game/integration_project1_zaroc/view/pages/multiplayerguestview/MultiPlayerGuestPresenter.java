package game.integration_project1_zaroc.view.pages.multiplayerguestview;

import game.integration_project1_zaroc.dao.PlayersDao;
import game.integration_project1_zaroc.dao.RoomDTO;
import game.integration_project1_zaroc.dao.ZarocDaoException;
import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.model.gameinfo.PawnColor;
import game.integration_project1_zaroc.model.players.Player;
import game.integration_project1_zaroc.view.sharedlogic.NavigationService;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.pawncolors.PawnColorPaths;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.profilePictures.ProfilePictures;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.Arrays;

public class MultiPlayerGuestPresenter {

    private MultiPlayerGuestView view;
    private AppController model;
    private PlayersDao playersDao;
    private String currentRoomCode;
    private RoomDTO currentRoomData;

    public MultiPlayerGuestPresenter(MultiPlayerGuestView view, AppController model) {
        this.view = view;
        this.model = model;
        this.playersDao = new PlayersDao();
        model.getColorOne().increaseCurrentIndex();
        updateView();
        addEventHandlers();
    }

    private void addEventHandlers() {
        for (Button button : Arrays.asList(view.getJoinButton(),view.getGuestColorPicker().getRightButton(),view.getGuestColorPicker().getLeftButton(),view.getReturnButton())){
            GeneralEventhandlers.addHoverEffect(button);
            GeneralEventhandlers.addSoundEffect(button, view.getResourceManager());
        }

        view.getGuestColorPicker().getRightButton().setOnAction(event -> {
            model.getColorOne().increaseCurrentIndex();
            if (currentRoomData != null && currentRoomData.getHostId() != 0) {
                if (PawnColor.values()[model.getColorOne().getCurrentIndex()] == currentRoomData.getHostColor()){
                    model.getColorOne().increaseCurrentIndex();
                }
            }
            updateView();
            updateGuestColor();
        });

        view.getGuestColorPicker().getLeftButton().setOnAction(event -> {
            model.getColorOne().decreaseCurrentIndex();
            if (currentRoomData != null && currentRoomData.getHostId() != 0) {
                if (PawnColor.values()[model.getColorOne().getCurrentIndex()] == currentRoomData.getHostColor()){
                    model.getColorOne().decreaseCurrentIndex();
                }
            }
            updateView();
            updateGuestColor();
        });

        view.getJoinButton().setOnAction(e -> {
            String code = view.getRoomCodeInput().getText().trim().toUpperCase();

            if (code.isEmpty()) {
                showAlert("enter legal code");
                return;
            }

            try {
                currentRoomCode = code;
                RoomDTO room = model.getMultiplayerService().getRoomDao().getRoomByCode(currentRoomCode);

                if (room == null) {
                    showAlert("Kamer niet gevonden!");
                    return;
                }

                if (room.getGameId() != 0) {
                    if (!model.getMultiplayerService().getRoomDao().isPlayerInGame(room.getGameId(), model.getPlayer1().getPlayerId())) {
                        showAlert("Je bent niet de juiste speler voor deze game!");
                        return;
                    }
                }

                model.getMultiplayerService().getRoomDao().joinRoom(currentRoomCode, model.getPlayer1().getPlayerId());
                updateGuestColor();

                view.getJoinButton().setDisable(true);
                view.getRoomCodeInput().setDisable(true);
                view.getStatusLabel().setText("Joined!");

                model.getMultiplayerService().startLobbyPolling(currentRoomCode, fetchedData -> {
                    Platform.runLater(() -> processRoomData(fetchedData));
                });

            } catch (Exception ex) {
                showAlert("Join mislukt: " + ex.getMessage());
                view.getStatusLabel().setText("JOIN FAILED");
            }
        });

        view.getReturnButton().setOnAction(e -> {
            model.getMultiplayerService().stopPolling();
            model.setOnlineMultiplayer(false);
            model.setPlayer2(null);
            NavigationService.closeWindow(this.view);
        });
    }

    private void updateView() {
        view.getGuestColorPicker().getImageView().setImage(view.getResourceManager().getPawnColor(PawnColorPaths.values()[model.getColorOne().getCurrentIndex()]));
        view.getGuestPfpView().setImage(view.getResourceManager().getProfilePicture(ProfilePictures.valueOf(model.getPlayer1().getProfilePicture())));
        view.getGuestName().setText(model.getPlayer1().getUsername());
    }

    private void updateGuestColor() {
        if (currentRoomCode != null && !currentRoomCode.isEmpty()) {
            try {
                model.getMultiplayerService().getRoomDao().setGuestColor(currentRoomCode, model.getPlayer1().getPlayerId(), PawnColor.values()[model.getColorOne().getCurrentIndex()]);
            } catch (ZarocDaoException e) {
                showAlert(e.toString());
            }
        }
    }

    private void processRoomData(RoomDTO fetchedData) {
        this.currentRoomData = fetchedData;

        if (view.getHostName().getText().contains("Waiting")) {
            try {
                Player host = playersDao.getPlayerById(currentRoomData.getHostId());
                if (host != null) {
                    view.getHostName().setText(host.getUsername());
                    view.getHostPfpView().setImage(view.getResourceManager().getProfilePicture(ProfilePictures.valueOf(host.getProfilePicture())));
                }
            } catch (Exception e) {
                System.err.println("Couldn't load host: " + e.getMessage());
            }
        }

        if (currentRoomData.getHostColor() != null) {
            view.getHostColor().setImage(view.getResourceManager().getPawnColor(PawnColorPaths.valueOf(currentRoomData.getHostColor().name())));
        }

        if ("PLAYING".equals(currentRoomData.getStatus())) {
            model.getMultiplayerService().stopPolling();
            handleGameStart();
        }
    }

    private void handleGameStart() {
        try {
            Player host = playersDao.getPlayerById(currentRoomData.getHostId());
            PawnColor hostColor = currentRoomData.getHostColor();
            PawnColor guestColor = PawnColor.values()[model.getColorOne().getCurrentIndex()];

            if (model.isContinueInMultiplayer()) {
                model.setPlayer2(host);
                model.setColorPlayerOne(guestColor);
                model.setColorPlayerTwo(hostColor);
                model.setOnlineMultiplayer(true);
                model.resumeGame(currentRoomData.getGameId(),false);
            } else {
                model.initOnlineGame(currentRoomData.getGameId(), host, guestColor, hostColor, false);
            }
            NavigationService.closeWindow(this.view);
        } catch (ZarocDaoException e) {
            showAlert("Kan spel niet starten: " + e.getMessage());
        }
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}