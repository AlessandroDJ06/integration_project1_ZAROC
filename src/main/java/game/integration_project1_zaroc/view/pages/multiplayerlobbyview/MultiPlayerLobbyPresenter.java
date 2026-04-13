package game.integration_project1_zaroc.view.pages.multiplayerlobbyview;

import game.integration_project1_zaroc.dao.PlayersDao;
import game.integration_project1_zaroc.dao.RoomDao;
import game.integration_project1_zaroc.dao.RoomDTO;
import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.model.gameinfo.PawnColor;
import game.integration_project1_zaroc.model.players.Player;
import game.integration_project1_zaroc.view.pages.boardview.GameBoardPresenter;
import game.integration_project1_zaroc.view.pages.boardview.GameBoardView;
import javafx.application.Platform;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MultiPlayerLobbyPresenter {
    private final MultiPlayerLobbyView view;
    private final AppController model;
    private final RoomDao roomDao;
    private final PlayersDao playersDao;
    private ScheduledExecutorService scheduler;
    private String currentRoomCode;
    private RoomDTO currentRoomData;

    public MultiPlayerLobbyPresenter(MultiPlayerLobbyView view, AppController model) {
        this.view = view;
        this.model = model;
        this.roomDao = new RoomDao();
        this.playersDao = new PlayersDao();

        setupRoleAndStart();
        addEventHandlers();
    }

    private void setupRoleAndStart() {
        if (model.isHost()) {
            System.out.println("--- SETUP ALS HOST ---");
            view.getRoomCodeInput().setVisible(false);
            view.getJoinButton().setVisible(false);
            view.getRoomCodeDisplayLabel().setVisible(true);
            view.getStartGameButton().setVisible(true);
            view.getStartGameButton().setDisable(true); // Wachten op gast

            currentRoomCode = generateRoomCode();
            System.out.println("Gegenereerde Room Code: " + currentRoomCode);
            view.getRoomCodeDisplayLabel().setText("CODE: " + currentRoomCode);
            view.getHostName().setText(model.getPlayer1().getUsername());

            try {
                roomDao.createRoom(currentRoomCode, model.getPlayer1().getPlayerId(), PawnColor.BLACK);
                this.currentRoomData = roomDao.getRoomByCode(currentRoomCode);
                System.out.println("Kamer succesvol opgeslagen in DB.");
            } catch (Exception e) {
                System.err.println("Fout bij aanmaken kamer: " + e.getMessage());
                e.printStackTrace();
            }

            startPolling();
        } else {
            System.out.println("--- SETUP ALS GUEST ---");
            view.getRoomCodeDisplayLabel().setVisible(false);
            view.getStartGameButton().setVisible(false);
            view.getRoomCodeInput().setVisible(true);
            view.getJoinButton().setVisible(true);
            view.getGuestName().setText(model.getPlayer1().getUsername());
        }
    }

    private void addEventHandlers() {
        // DEBUG: Wisselen van rol
        view.getDebugRoleButton().setOnAction(e -> {
            System.out.println("DEBUG: Wisselen van rol...");
            stopPolling();
            model.setHost(!model.isHost());
            model.setPlayer2(null);
            setupRoleAndStart();
        });

        // JOIN: Voor de gast
        view.getJoinButton().setOnAction(e -> {
            String code = view.getRoomCodeInput().getText().trim().toUpperCase();
            if (!code.isEmpty()) {
                try {
                    System.out.println("Gast probeert te joinen met code: " + code);
                    roomDao.joinRoom(code, model.getPlayer1().getPlayerId());
                    currentRoomCode = code;
                    view.getJoinButton().setDisable(true);
                    view.getRoomCodeInput().setDisable(true);
                    startPolling();
                } catch (Exception ex) {
                    System.err.println("Joinen mislukt: " + ex.getMessage());
                    view.getStatusLabel().setText("FOUT BIJ JOINEN");
                }
            }
        });

        view.getStartGameButton().setOnAction(e -> {
            try {
                System.out.println("Host klikt op START. GameID aanmaken...");
                int gameId = roomDao.startGame(currentRoomData.getRoomId());
                stopPolling();

                Player guest = playersDao.getPlayerById(currentRoomData.getGuestId());
                model.initOnlineGame(gameId, guest, PawnColor.BLACK, PawnColor.WHITE, true);

                System.out.println("Game " + gameId + " gestart. Host gaat naar bord.");
                navigateToBoard();
            } catch (Exception ex) {
                System.err.println("Starten mislukt: " + ex.getMessage());
                ex.printStackTrace();
                view.getStatusLabel().setText("START MISLUKT");
            }
        });

        view.getReturnButton().setOnAction(e -> {
            stopPolling();
        });
    }

    private void startPolling() {
        stopPolling();
        System.out.println("Poller gestart voor code: " + currentRoomCode);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                RoomDTO fetchedData = roomDao.getRoomByCode(currentRoomCode);

                Platform.runLater(() -> {
                    if (fetchedData == null) return;
                    this.currentRoomData = fetchedData;

                    // --- LOGICA VOOR HOST ---
                    if (model.isHost()) {
                        if (currentRoomData.getGuestId() != 0) {
                            // Er is een gast! Zet de startknop aan.
                            if (view.getStartGameButton().isDisable()) {
                                System.out.println("Gast gedetecteerd (ID: " + currentRoomData.getGuestId() + "). Startknop aan.");
                                view.getStartGameButton().setDisable(false);
                                view.getStatusLabel().setText("Gast is verbonden!");
                                updateGuestInfo();
                            }
                        }
                    }

                    if (!model.isHost()) {
                        if (view.getHostName().getText().contains("Wachten")) {
                            updateHostInfo();
                        }

                        if ("PLAYING".equals(currentRoomData.getStatus())) {
                            System.out.println("Host heeft status op PLAYING gezet. Gast gaat naar bord.");
                            stopPolling();
                            handleGuestStart();
                        }
                    }
                });
            } catch (Exception e) {
                System.err.println("Fout in poller: " + e.getMessage());
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void updateGuestInfo() {
        try {
            Player g = playersDao.getPlayerById(currentRoomData.getGuestId());
            if (g != null) view.getGuestName().setText(g.getUsername());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateHostInfo() {
        try {
            Player h = playersDao.getPlayerById(currentRoomData.getHostId());
            if (h != null) view.getHostName().setText(h.getUsername());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void handleGuestStart() {
        try {
            Player h = playersDao.getPlayerById(currentRoomData.getHostId());
            model.initOnlineGame(currentRoomData.getGameId(), h, PawnColor.WHITE, PawnColor.BLACK, false);
            navigateToBoard();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void navigateToBoard() {
        Platform.runLater(() -> {
            try {
                GameBoardView gameBoardView = new GameBoardView(view.getResourceManager());
                new GameBoardPresenter(gameBoardView, model);
                view.getScene().setRoot(gameBoardView);
            } catch (Exception e) {
                System.err.println("Navigatie mislukt: " + e.getMessage());
            }
        });
    }

    private void stopPolling() {
        if (scheduler != null) {
            System.out.println("Poller wordt gestopt.");
            scheduler.shutdownNow();
        }
    }

    private String generateRoomCode() {
        return Integer.toHexString(new Random().nextInt(0xFFFFF)).toUpperCase();
    }
}