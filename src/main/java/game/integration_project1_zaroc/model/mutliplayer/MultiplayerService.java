package game.integration_project1_zaroc.model.mutliplayer;

import game.integration_project1_zaroc.dao.MultiplayerDao;
import game.integration_project1_zaroc.dao.MultiplayerMove;
import game.integration_project1_zaroc.dao.RoomDao;
import game.integration_project1_zaroc.dao.RoomDTO;
import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.model.boardinfo.Peg;
import game.integration_project1_zaroc.model.gamelogic.Move;
import game.integration_project1_zaroc.model.gamelogic.MoveNumber;
import game.integration_project1_zaroc.utils.Observable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MultiplayerService extends Observable {
    private final AppController model;
    private final MultiplayerDao multiplayerDao;
    private final RoomDao roomDao;

    private ScheduledExecutorService scheduler;
    private volatile int lastKnownMoveCount = 0;

    public MultiplayerService(AppController model) {
        this.model = model;
        this.multiplayerDao = new MultiplayerDao();
        this.roomDao = new RoomDao();
    }


    public String generateRoomCode() {
        return Integer.toHexString(new Random().nextInt(0xFFFFF)).toUpperCase();
    }

    public void startLobbyPolling(String roomCode, Consumer<RoomDTO> onRoomUpdated) {
        stopPolling();
        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            try {
                RoomDTO fetchedData = roomDao.getRoomByCode(roomCode);

                if (fetchedData != null && onRoomUpdated != null) {
                    onRoomUpdated.accept(fetchedData);
                }
            } catch (Exception e) {
                System.err.println("Fout in lobby poller: " + e.getMessage());
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public RoomDao getRoomDao() {
        return roomDao;
    }

    public void startTurnPolling(int gameId, int currentLocalMoves, String myUsername) {
        this.lastKnownMoveCount = currentLocalMoves;
        stopPolling();

        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("Polling... gameId=" + gameId + " offset=" + lastKnownMoveCount + " username=" + myUsername);
                List<MultiplayerMove> newMoves = multiplayerDao.fetchNewMoves(gameId, lastKnownMoveCount, myUsername);
                System.out.println("new moves found: " + newMoves.size());

                if (!newMoves.isEmpty()) {
                    lastKnownMoveCount += newMoves.size();

                    for (MultiplayerMove data : newMoves) {
                        try {
                            Move move = convertToMove(data);
                            model.getGame().executeMoveUnfinishedGame(move);
                            if (data.getMoveNumber() == 2) {
                                model.getGame().switchCurrentPlayer();
                            }
                            notifyObservers(move);

                        } catch (Exception e) {
                            System.err.println("Fout bij verwerken van remote zet: " + e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Fout tijdens turn polling: " + e.getMessage());
            }
        }, 0, 2, TimeUnit.SECONDS);
    }


    public void stopPolling() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }

    private Move convertToMove(MultiplayerMove data) {
        String[] startCoords = data.getStartLocation().split(",");
        String[] endCoords = data.getEndLocation().split(",");

        int startX = Integer.parseInt(startCoords[0].trim());
        int startY = Integer.parseInt(startCoords[1].trim());
        int endX = Integer.parseInt(endCoords[0].trim());
        int endY = Integer.parseInt(endCoords[1].trim());

        Peg start = model.getGame().getBoard().getPegPosition(startY, startX);
        Peg dest = model.getGame().getBoard().getPegPosition(endY, endX);

        MoveNumber moveNumber = (data.getMoveNumber() == 1) ? MoveNumber.FIRST_MOVE : MoveNumber.SECOND_MOVE;
        Move move = new Move(moveNumber, start, dest);

        move.setStartTime(data.getStartTime());
        move.setEndTime(data.getEndTime());

        return move;
    }

    public void setLastKnownMoveCount(int count) {
        this.lastKnownMoveCount = count;
    }

    public int getLastKnownMoveCount() {
        return lastKnownMoveCount;
    }
}