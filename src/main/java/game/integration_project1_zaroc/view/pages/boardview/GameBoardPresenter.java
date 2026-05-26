package game.integration_project1_zaroc.view.pages.boardview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.model.boardinfo.Pawn;
import game.integration_project1_zaroc.model.boardinfo.Peg;
import game.integration_project1_zaroc.model.gameinfo.GameStatus;
import game.integration_project1_zaroc.model.gamelogic.Move;
import game.integration_project1_zaroc.model.gamelogic.Turn;
import game.integration_project1_zaroc.model.players.AIPlayer;
import game.integration_project1_zaroc.model.players.Player;
import game.integration_project1_zaroc.utils.Observer;
import game.integration_project1_zaroc.view.pages.pausescreenview.PauseScreenPresenter;
import game.integration_project1_zaroc.view.pages.pausescreenview.PauseScreenView;
import game.integration_project1_zaroc.view.pages.winscreenview.WinScreenPresenter;
import game.integration_project1_zaroc.view.pages.winscreenview.WinScreenView;
import game.integration_project1_zaroc.view.pages.winwarningview.WinWarningPresenter;
import game.integration_project1_zaroc.view.pages.winwarningview.WinWarningView;
import game.integration_project1_zaroc.view.sharedlogic.NavigationService;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.pawncolors.PawnColorPaths;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.pawncolors.PawnSideViews;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.profilePictures.ProfilePictures;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;

/**
 * presenter for the game board, handles all interaction between the player and the board
 * it manages three types of turns: local human, AI, and remote (online multiplayer)
 * for online multiplayer it registers itself as an observer of MultiplayerService
 * so it gets notified whenever the poller picks up a new opponent move
 * the board is disabled while waiting for the remote player and re-enabled when it's your turn
 *
 * timers:
 *   - afkTimer    -> auto-executes a random move if the player is idle too long
 *   - undoTimer   -> gives the player a short window to undo after each move (disabled in online mode)
 */
public class GameBoardPresenter implements Observer {
    private GameBoardView view;
    private AppController model;
    private List<HBox> rows;
    private List<Button> buttons;
    private Timeline player1Animation;
    private Timeline player2Animation;
    private Timeline highlightAnimation;
    private Timeline undoTimer;
    private int remainingUndoSeconds;
    private Timeline afkTimer;
    private final int AFK_TIME_LIMIT = 15;
    private int remainingAfkSeconds;

    private ImageView selectedPawn;
    private boolean player1Warned = false;
    private boolean player2Warned = false;
    private boolean isPaused = false;

    private WinWarningPresenter winWarningPresenter;

    public GameBoardPresenter(GameBoardView view, AppController model) {
        this.view = view;
        this.model = model;
        view.getResourceManager().addObserver(this);
        this.rows = Arrays.asList(view.getPegRowFour(), view.getPegRowThree(), view.getPegRowTwo());
        this.buttons = Arrays.asList(view.getUndoButton(), view.getSettingsButton(), view.getInfoButton());
        this.player1Animation = createPulseAnimation(view.getPlayersPlayingComponent().getFirstPlayer());
        this.player2Animation = createPulseAnimation(view.getPlayersPlayingComponent().getSecondPlayer());

        this.selectedPawn = null;

        if (this.model.getMultiplayerService() != null) {
            this.model.getMultiplayerService().addObserver(this);

            if (this.model.isOnlineMultiplayer()) {
                String myUsername = model.getPlayer1().getUsername();
                int gameId = model.getOnlineGameId();
                int offset = model.getMultiplayerService().getLastKnownMoveCount();
                this.model.getMultiplayerService().startTurnPolling(gameId, offset, myUsername);
            }
        }
        view.getResourceManager().getMusicManager().changeMusic();
        updateView();
        addEventHandlers();
        processTurn();
    }

    @Override
    public void update(Object args) {
        Platform.runLater(() -> {
            updateView();
            processTurn();
        });
    }

    private void addEventHandlers() {

        view.getSettingsButton().setOnAction(actionEvent -> {
            NavigationService.navigateToSettings(view.getResourceManager(), this.model).showAndWait();
        });

        view.getInfoButton().setOnAction(event -> {
            NavigationService.navigateToRules(view.getResourceManager(), this.model).showAndWait();
        });

        view.getUndoButton().setOnAction(e -> {
            undoTimer.stop();
            view.getUndoTimer().setVisible(false);
            view.getSkipButton().setVisible(false);
            view.getSkipButton().setDisable(true);
            view.getUndoButton().setDisable(true);

            model.getGame().undoMove();
            view.getResourceManager().getSfxManager().playUndoSound();
            updateView();

            startAfkTimer();
        });

        view.getPlayersPlayingComponent().getPauseButton().setOnAction(event -> {

            handlePause(false, event);

        });
        view.getSkipButton().setOnAction(event -> {
            undoTimer.stop();
            view.getUndoTimer().setVisible(false);
            view.getSkipButton().setVisible(false);
            view.getSkipButton().setDisable(true);
            view.getUndoButton().setDisable(true);

            if (model.getGame().getCurrentTurn().getSecondMove() != null) {
                model.getGame().switchCurrentPlayer();
                processTurn();
            }
        });
        GeneralEventhandlers.addHoverEffect(view.getPlayersPlayingComponent().getPauseButton());
        GeneralEventhandlers.addHoverEffect(view.getSkipButton());


        for (Button button : buttons) {
            GeneralEventhandlers.addHoverEffect(button);
            GeneralEventhandlers.addSoundEffect(button, view.getResourceManager());
        }

        for (HBox row : rows) {
            row.setOnMouseEntered(mouseEvent -> {
                row.setScaleY(1.5);
                row.setScaleX(1.5);
                row.setSpacing(30);
            });

            row.setOnMouseExited(mouseEvent -> {
                row.setScaleY(1);
                row.setScaleX(1);
                row.setSpacing(60);
            });
        }

        for (ImageView position : view.getBoard().getPegPositions()) {
            position.setOnMouseClicked(event -> {
                int col = GridPane.getColumnIndex(position);
                int row = GridPane.getRowIndex(position);
                handlePegClick(col, row);
            });
        }

    }

    private void handlePause(boolean close, Event event) {
        event.consume();

        pauzeAfkTimer();
        player1Animation.pause();
        player2Animation.pause();
        isPaused = true;
        view.getBoard().getBoard().setDisable(true);
        if (undoTimer != null && undoTimer.getStatus() == Animation.Status.RUNNING) {
            undoTimer.pause();
        }

        PauseScreenView pauseScreenView = new PauseScreenView(view.getResourceManager());
        PauseScreenPresenter pauseScreenPresenter = new PauseScreenPresenter(pauseScreenView, model);
        pauseScreenPresenter.setGameBoardPresenter(this);

        if (model.getGame().isAllowedSave()) {
            pauseScreenView.getNoteUnfinishedGame().setText("NOTE: Current game will be added to Unfinished Games");
        }

        Scene pauseScene = new Scene(pauseScreenView);
        pauseScene.setFill(Color.TRANSPARENT);
        Stage pauseStage = new Stage();
        pauseStage.setScene(pauseScene);
        pauseStage.initOwner(view.getScene().getWindow());
        pauseStage.initStyle(StageStyle.TRANSPARENT);
        pauseStage.initModality(Modality.APPLICATION_MODAL);
        if (close) {
            pauseStage.show();
            Alert stopWindow = new Alert(Alert.AlertType.CONFIRMATION);
            stopWindow.initOwner(pauseStage);
            stopWindow.initModality(Modality.WINDOW_MODAL);
            stopWindow.setHeaderText("You're closing the application.");
            stopWindow.setContentText("Are you sure?");
            stopWindow.setTitle("WARNING!");
            stopWindow.getButtonTypes().clear();
            stopWindow.getButtonTypes().addAll(new ButtonType("Yes"), new ButtonType("No"));
            stopWindow.showAndWait();

            if (stopWindow.getResult() != null && stopWindow.getResult().getText().equals("Yes")) {
               view.getScene().getWindow().hide();
            } else {
                pauseStage.hide();
                pauseStage.showAndWait();
            }
        } else {
            pauseStage.showAndWait();
        }



        if (pauseScreenPresenter.isContinued()) {
            isPaused=false;
            view.getBoard().getBoard().setDisable(false);
            if (model.getGame().getCurrentTurn().getCurrentPlayer().getUsername()
                    .equals(model.getGame().getParticipation1().getPlayer().getUsername())) {
                player1Animation.play();
            } else {
                player2Animation.play();
            }
            if (undoTimer != null && undoTimer.getStatus() == Animation.Status.PAUSED) {
                undoTimer.play();
            }else{
            continueAfkTimer();
            }
            if (model.getGame().getCurrentTurn().getCurrentPlayer() instanceof AIPlayer) {
                processTurn();
            }

        }
    }

    public void attachCloseHandler(Stage stage) {
        stage.setOnCloseRequest(event ->
                { if(stage.getScene().getRoot() == view){
                    handlePause(true, event);
                }
                });
    }


    //----------------------------------------------------------------------------------------------
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~UPDATE METHODS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //----------------------------------------------------------------------------------------------
    private void updateView() {
        if (model.getGame() != null) {
            view.getPlayersPlayingComponent().setFirstPlayer(
                    model.getGame().getParticipation1().getPlayer().getUsername()
            );

            view.getPlayersPlayingComponent().setSecondPlayer(
                    model.getGame().getParticipation2().getPlayer().getUsername()
            );
        }

        if (!model.isOnlineMultiplayer() || model.getGame().getCurrentTurn() != null) {
            if (model.getGame().getCurrentTurn().getCurrentPlayer().getUsername()
                    .equals(model.getGame().getParticipation1().getPlayer().getUsername())) {
                player1Animation.play();
                player2Animation.stop();
                view.getPlayersPlayingComponent().getSecondPlayer().setScaleX(1);
                view.getPlayersPlayingComponent().getSecondPlayer().setScaleY(1);
            } else {
                player1Animation.stop();
                player2Animation.play();
                view.getPlayersPlayingComponent().getFirstPlayer().setScaleX(1);
                view.getPlayersPlayingComponent().getFirstPlayer().setScaleY(1);
            }
        }

        String pfp1 = model.getPlayer1().getProfilePicture();
        String pfp2 = model.getPlayer2().getProfilePicture();
        Image colorPlayerOne = view.getResourceManager().getPawnSideView(PawnSideViews.values()[model.getColorOne().getCurrentIndex()]);
        Image colorPlayerTwo = view.getResourceManager().getPawnSideView(PawnSideViews.values()[model.getColorTwo().getCurrentIndex()]);
        if (pfp1 != null) {
            view.getPlayersPlayingComponent().setPlayerOnePfp(ProfilePictures.valueOf(pfp1));
        } else {
            view.getPlayersPlayingComponent().setPlayerOnePfp(ProfilePictures.EMPTY);
        }

        if (pfp2 != null) {
            view.getPlayersPlayingComponent().setPlayerTwoPfp(ProfilePictures.valueOf(pfp2));
        } else {
            view.getPlayersPlayingComponent().setPlayerTwoPfp(ProfilePictures.EMPTY);
        }

        view.getPlayersPlayingComponent().setPointPlayerOne(colorPlayerOne);
        view.getPlayersPlayingComponent().setColorPlayerTwo(colorPlayerTwo);
        renderBoard();
    }

    private void renderBoard() {
        Peg[][] allPegs = model.getGame().getBoard().getAllPegs();

        view.getBoard().getBoard().getChildren().removeIf(node ->
                node.getStyleClass().contains("is-pion")
        );

        for (VBox container : view.getPegContainers()) {
            container.getChildren().clear();
        }

        int currentContainerIdx = 0;

        for (int r = 0; r < allPegs.length; r++) {
            for (int c = 0; c < allPegs[r].length; c++) {

                if (allPegs[r][c] == null) continue;

                Peg currentPeg = allPegs[r][c];
                ArrayList<Pawn> logicPawns = currentPeg.getPawns();

                for (int laag = 0; laag < logicPawns.size(); laag++) {
                    Pawn logicPawn = logicPawns.get(laag);
                    PawnColorPaths kleur = PawnColorPaths.values()[logicPawn.getPawnColor().ordinal()];

                    ImageView pionImg = view.getResourceManager().getPawnImageView(kleur);
                    pionImg.getStyleClass().add("is-pion");
                    pionImg.setMouseTransparent(true);

                    GridPane.setHalignment(pionImg, HPos.CENTER);
                    GridPane.setValignment(pionImg, VPos.CENTER);
                    pionImg.setTranslateY(-8 * laag);

                    view.getBoard().getBoard().add(pionImg, currentPeg.getXPosition(), currentPeg.getYPosition());
                }

                boolean hasSideView = (r < 3);

                if (hasSideView && currentContainerIdx < view.getPegContainers().size()) {
                    VBox currentContainer = view.getPegContainers().get(currentContainerIdx);

                    for (Pawn logicPawn : logicPawns) {
                        PawnSideViews sideColor = PawnSideViews.values()[logicPawn.getPawnColor().ordinal()];
                        ImageView sideImg = new ImageView(view.getResourceManager().getPawnSideView(sideColor));
                        currentContainer.getChildren().addFirst(sideImg);
                    }
                    currentContainerIdx++;
                }
            }
        }
    }
    public void closeWarningPopup() {
        if (winWarningPresenter != null) {
            winWarningPresenter.close();
            winWarningPresenter = null;
        }
    }

    private void showWinner() {
        closeWarningPopup();
        WinScreenView winScreenView = new WinScreenView(this.view.getResourceManager());
        new WinScreenPresenter(winScreenView, model);

        Scene winScene = new Scene(winScreenView);
        winScene.setFill(Color.TRANSPARENT);
        Stage winStage = new Stage();
        winStage.initOwner(view.getScene().getWindow());
        winStage.initStyle(StageStyle.TRANSPARENT);
        winStage.initModality(Modality.APPLICATION_MODAL);
        winStage.setScene(winScene);
        winStage.showAndWait();
    }

    private void showWinWarning() {
        WinWarningView winWarningView = new WinWarningView(this.view.getResourceManager());
        winWarningPresenter = new WinWarningPresenter(model, winWarningView);

        winWarningView.getWinWarning().setText(model.getGame().getPlayerCloseToWinning().getUsername().toUpperCase() + " IS CLOSE TO WINNING!");
        Scene warningScene = new Scene(winWarningView);
        warningScene.setFill(Color.TRANSPARENT);
        Stage warningStage = new Stage();
        warningStage.initStyle(StageStyle.TRANSPARENT);
        warningStage.initOwner(view.getScene().getWindow());
        warningStage.initModality(Modality.NONE);
        warningStage.setScene(warningScene);

        warningStage.setOnShown(e -> {
            Window owner = view.getScene().getWindow();

            double x = owner.getX() + owner.getWidth() - warningStage.getWidth() - 10;
            double y = owner.getY() + owner.getHeight() - warningStage.getHeight() - 10;

            warningStage.setX(x);
            warningStage.setY(y);
        });

        warningStage.show();
    }

    private void showWinWarningIfNeeded() {
        Player player = model.getGame().getPlayerCloseToWinning();
        if (player == null) return;

        boolean isPlayer1 = player.getUsername().equals(model.getPlayer1().getUsername());

        if (isPlayer1 && !player1Warned) {
            player1Warned = true;
            showWinWarning();
        } else if (!isPlayer1 && !player2Warned) {
            player2Warned = true;
            showWinWarning();
        }
    }

    //----------------------------------------------------------------------------------------------
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~CURRENT PLAYER HANDLE~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //----------------------------------------------------------------------------------------------
    /**
     * the main routing method that decides what happens at the start of each turn
     * checks three cases:
     *   - AI player      -> disables the board and runs executeAiLogic() on a background thread
     *   - remote player  -> disables the board and waits for the poller to notify us
     *   - local player   -> enables the board and starts the afk timer
     *
     * in online multiplayer the guest may have no current turn yet when the board first opens
     * (the host hasn't started their turn yet), in that case we just disable the board and return
     */
    private void processTurn() {
        if (model.isOnlineMultiplayer() && model.getGame().getCurrentTurn() == null) {
            view.getBoard().getBoard().setDisable(true);
            view.getUndoButton().setDisable(true);
            return;
        }

        updateView();

        if (model.getGame().getStatus() == GameStatus.ENDED) {
            if (model.getMultiplayerService() != null) {
                model.getMultiplayerService().stopPolling();
                showWinner();
            }
            return;
        }

        Player currentPlayer = model.getGame().getCurrentTurn().getCurrentPlayer();

        if (currentPlayer instanceof AIPlayer) {
            stopAfkTimer();
            view.getUndoButton().setDisable(true);
            view.getBoard().getBoard().setDisable(true);
            executeAiLogic((AIPlayer) currentPlayer);
        } else if (isRemotePlayer(currentPlayer)) {
            stopAfkTimer();
            view.getUndoButton().setDisable(true);
            view.getBoard().getBoard().setDisable(true);
        } else {
            view.getBoard().getBoard().setDisable(false);
            view.getUndoButton().setDisable(true);
            startAfkTimer();
            if (isPaused) {
                pauzeAfkTimer();
            }
        }
    }

    /**
     * checks if the given player is the remote opponent in an online game
     * returns false for local/AI games and also false if player1 is the current player
     * (player1 is always the local player)
     * @param p -> player to check
     * @return -> true if this player's moves come from the poller, not from local input
     */
    private boolean isRemotePlayer(Player p) {
        if (!model.isOnlineMultiplayer()) return false;
        if (model.getGame() == null || model.getGame().getGameId() == -1) return false;
        return !p.getUsername().equals(model.getPlayer1().getUsername());
    }

    /**
     * counts the total number of individual moves across all turns in the current game
     * used to calculate the polling offset when starting turn polling after a resume
     * note: this counts ALL moves (both players), not just opponent moves,
     * so it should only be used when the context makes that correct
     * @return -> total move count across all turns
     */
    private int calculateTotalMoves() {
        int moveCount = 0;
        if (model.getGame() != null && model.getGame().getTurns() != null) {
            for (Turn t : model.getGame().getTurns()) {
                if (t.getFirstMove() != null) moveCount++;
                if (t.getSecondMove() != null) moveCount++;
            }
        }
        return moveCount;
    }
    //----------------------------------------------------------------------------------------------
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~HANDLE   AI    MOVE~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //----------------------------------------------------------------------------------------------
    /**
     * runs the AI move calculation on a background daemon thread to avoid freezing the UI
     * once the AI decides its turn, both moves are applied on the JavaFX thread via Platform.runLater()
     * a short pause of 800ms is added between move 1 and move 2 so the player can see what happened
     * after both moves are done it switches to the next player and calls processTurn() again
     * @param ai -> the AI player whose decideTurn() method we call
     */
    private void executeAiLogic(AIPlayer ai) {
        Thread aiThread = new Thread(() -> {
            Turn bestTurn = ai.decideTurn(model.getGame());

            Platform.runLater(() -> {
                if (bestTurn != null) {
                    executeSingleMove(bestTurn.getFirstMove());
                    updateView();
                    view.getResourceManager().getSfxManager().playPawnMove();

                    if (bestTurn.getSecondMove() != null) {
                        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.millis(800));
                        pause.setOnFinished(event -> {
                            executeSingleMove(bestTurn.getSecondMove());
                            updateView();
                            view.getResourceManager().getSfxManager().playPawnMove();
                            if(model.getGame().getStatus() == GameStatus.ENDED){
                                Platform.runLater(() -> showWinner());
                                return;
                            }
                            showWinWarningIfNeeded();
                            model.getGame().switchCurrentPlayer();
                            if (!isPaused) {
                                processTurn();
                            }

                        });
                        pause.play();
                    } else {
                        showWinWarningIfNeeded();
                        model.getGame().switchCurrentPlayer();
                        if (!isPaused) {
                            processTurn();
                        }
                    }
                }
            });
        });
        aiThread.setDaemon(true);
        aiThread.start();
    }

    /**
     * applies a single move to the game by looking up the actual peg objects from their coordinates
     * this is a shared helper used by both AI logic and the remote move processor
     * it calls selectStartPeg() and executeMove() on the game model just like a human click would
     * @param m -> the move to execute, does nothing if null or if the start peg is empty
     */
    private void executeSingleMove(Move m) {
        if (m == null) return;
        Peg start = model.getGame().getBoard().getPegPosition(m.getStartPeg().getYPosition(), m.getStartPeg().getXPosition());
        Peg dest = model.getGame().getBoard().getPegPosition(m.getDestinationPeg().getYPosition(), m.getDestinationPeg().getXPosition());

        if (start != null && dest != null && !start.getPawns().isEmpty()) {
            model.getGame().selectStartPeg(start);
            model.getGame().executeMove(dest);
        }
    }

    //----------------------------------------------------------------------------------------------
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~HANDLE HUMAN MOVE~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //----------------------------------------------------------------------------------------------
    /**
     * handles a click on any peg position on the board by the local human player
     * works in two stages:
     *   1. first click  -> selects a pawn (if there is one), highlights legal moves
     *   2. second click -> if the destination is legal, executes the move and starts the undo timer
     *                      if illegal, deselects the current pawn
     *
     * returns immediately if the current turn already has both moves filled in
     * @param col -> grid column of the clicked peg
     * @param row -> grid row of the clicked peg
     */
    private void handlePegClick(int col, int row) {

        if (model.getGame().getCurrentTurn().getSecondMove() != null) {
            return;
        }

        if (selectedPawn == null) {
            ImageView bovensteImg = getTopPawn(col, row);

            if (bovensteImg != null && bovensteImg.getStyleClass().contains("is-pion")) {
                selectedPawn = bovensteImg;
                selectedPawn.setOpacity(0.5);
                highLightLegalMoves();
            }
        } else {
            int startCol = GridPane.getColumnIndex(selectedPawn);
            int startRow = GridPane.getRowIndex(selectedPawn);
            Peg startPeg = model.getGame().getBoard().getAllPegs()[startRow][startCol];
            Peg destinationPeg = model.getGame().getBoard().getAllPegs()[row][col];

            List<Move> legalMoves = model.getGame().getLegalMoves(startPeg);
            boolean isLegal = false;

            for (Move m : legalMoves) {
                if (m.getDestinationPeg().equals(destinationPeg)) {
                    isLegal = true;
                    break;
                }
            }

            if (isLegal) {
                stopAfkTimer();

                model.getGame().selectStartPeg(startPeg);
                model.getGame().executeMove(destinationPeg);

                view.getResourceManager().getSfxManager().playPawnMove();
                updateView();

                if (model.getGame().getStatus() == GameStatus.ENDED) {
                    showWinner();
                    return;
                }
                showWinWarningIfNeeded();
                startUndoTimer();
            } else {
                selectedPawn.setOpacity(1.0);
            }

            selectedPawn = null;
            clearHighlights();
        }
    }

    /**
     * finds the topmost ImageView pawn at a given grid position
     * iterates all children of the board GridPane and returns the last one found at (col, row)
     * since pawns are stacked the last one in the list is visually on top
     * @param col -> grid column
     * @param row -> grid row
     * @return -> the top pawn ImageView or null if the position is empty
     */
    private ImageView getTopPawn(int col, int row) {
        ImageView bovenste = null;
        for (Node node : view.getBoard().getBoard().getChildren()) {
            if (node instanceof ImageView && isInPosition(node, col, row)) {
                bovenste = (ImageView) node;
            }
        }
        return bovenste;
    }

    private boolean isInPosition(Node node, int col, int row) {
        Integer columnPos = GridPane.getColumnIndex(node);
        Integer rowPos = GridPane.getRowIndex(node);
        return columnPos != null && rowPos != null && columnPos == col && rowPos == row;
    }

    private void startAfkTimer() {
        stopAfkTimer();
        remainingAfkSeconds = AFK_TIME_LIMIT;

        view.getPlayersPlayingComponent().getAfkTimer().setVisible(true);
        view.getPlayersPlayingComponent().getAfkTimer().setText("00:" + String.format("%02d", remainingAfkSeconds));

        afkTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            remainingAfkSeconds--;
            view.getPlayersPlayingComponent().getAfkTimer().setText("00:" + String.format("%02d", remainingAfkSeconds));

            if (remainingAfkSeconds <= 0) {
                stopAfkTimer();
                selectedPawn = null;
                clearHighlights();
                model.getGame().executeRandomMove();
                updateView();

                if (model.getGame().getStatus() == GameStatus.ENDED) {
                    showWinner();
                    return;
                }

                view.getUndoTimer().setVisible(false);
                view.getUndoButton().setDisable(true);

                if (model.getGame().getCurrentTurn().getSecondMove() != null) {
                    model.getGame().switchCurrentPlayer();
                    processTurn();
                } else {
                    startAfkTimer();
                }
            }
        }));

        afkTimer.setCycleCount(AFK_TIME_LIMIT);
        afkTimer.play();
    }

    private void stopAfkTimer() {
        if (afkTimer != null) {
            afkTimer.stop();
        }
        view.getPlayersPlayingComponent().getAfkTimer().setVisible(false);
    }

    private void pauzeAfkTimer() {
        if (afkTimer != null) {
            afkTimer.pause();
        }
    }

    private void continueAfkTimer() {
        if (afkTimer != null) {
            afkTimer.play();
            view.getPlayersPlayingComponent().getAfkTimer().setVisible(true);
        }
    }

    private void startUndoTimer() {
        if (model.isOnlineMultiplayer()) {
            view.getUndoTimer().setVisible(false);
            view.getSkipButton().setVisible(false);
            view.getSkipButton().setDisable(true);
            view.getUndoButton().setDisable(true);

            if (model.getGame().getCurrentTurn().getSecondMove() != null) {
                model.getGame().switchCurrentPlayer();
                processTurn();
            }
            return;
        }

        if (undoTimer != null) {
            undoTimer.stop();
        }
        remainingUndoSeconds = 5;

        view.getUndoTimer().setVisible(true);
        view.getSkipButton().setVisible(model.getGame().getCurrentTurn().getSecondMove() != null);
        view.getSkipButton().setDisable(model.getGame().getCurrentTurn().getSecondMove() == null);
        view.getUndoTimer().setText("00:0" + remainingUndoSeconds);
        view.getUndoButton().setDisable(false);

        undoTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {

            remainingUndoSeconds--;
            view.getUndoTimer().setText("00:0" + remainingUndoSeconds);

            if (remainingUndoSeconds <= 0) {
                undoTimer.stop();
                view.getUndoTimer().setVisible(false);
                view.getSkipButton().setVisible(false);
                view.getSkipButton().setDisable(true);
                view.getUndoButton().setDisable(true);

                if (model.getGame().getCurrentTurn().getSecondMove() != null) {
                    model.getGame().switchCurrentPlayer();
                    processTurn();
                } else {
                    startAfkTimer();
                }

            }
        }));
        undoTimer.setCycleCount(5);
        undoTimer.play();
    }

    //----------------------------------------------------------------------------------------------
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~HIGH LIGHT~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //----------------------------------------------------------------------------------------------

    private void highLightLegalMoves() {
        clearHighlights();

        if (selectedPawn != null) {
            selectedPawn.setOpacity(0.75);

            int x = GridPane.getColumnIndex(selectedPawn);
            int y = GridPane.getRowIndex(selectedPawn);
            Peg start = model.getGame().getBoard().getAllPegs()[y][x];
            List<Move> legalMoves = model.getGame().getLegalMoves(start);

            List<Node> targets = new ArrayList<>();
            for (Move move : legalMoves) {
                int xDest = move.getDestinationPeg().getXPosition();
                int yDest = move.getDestinationPeg().getYPosition();
                for (Node node : view.getBoard().getBoard().getChildren()) {
                    if (isInPosition(node, xDest, yDest)) {
                        targets.add(node);
                    }
                }
            }
            if (!targets.isEmpty()) {
                startHighlightAnimation(targets);
            }
        }
    }

    private void clearHighlights() {
        if (highlightAnimation != null) {
            highlightAnimation.stop();
        }

        for (Node node : view.getBoard().getBoard().getChildren()) {
            node.setOpacity(1.0);
            node.opacityProperty().unbind();
        }
    }

    //----------------------------------------------------------------------------------------------
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ANIMATIONS~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //----------------------------------------------------------------------------------------------
    private Timeline createPulseAnimation(Label name) {
        Timeline pulse = new Timeline();
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.setAutoReverse(true);

        KeyValue kvX = new KeyValue(name.scaleXProperty(), 1.2);
        KeyValue kvY = new KeyValue(name.scaleYProperty(), 1.2);

        KeyFrame kf = new KeyFrame(Duration.millis(1000), kvX, kvY);
        pulse.getKeyFrames().add(kf);

        return pulse;
    }

    private void startHighlightAnimation(List<Node> targets) {
        if (highlightAnimation != null) {
            highlightAnimation.stop();
        }

        highlightAnimation = new Timeline();
        highlightAnimation.setCycleCount(Timeline.INDEFINITE);
        highlightAnimation.setAutoReverse(true);

        for (Node node : targets) {
            KeyValue kv = new KeyValue(node.opacityProperty(), 0.7);
            KeyFrame kf = new KeyFrame(Duration.millis(1000), kv);
            highlightAnimation.getKeyFrames().add(kf);
        }

        highlightAnimation.play();
    }

    @Override
    public void updateLayout(Object args) {
        view.layoutNodes();
    }
}