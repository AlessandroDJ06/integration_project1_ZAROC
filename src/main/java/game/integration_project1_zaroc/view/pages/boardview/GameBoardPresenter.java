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
import game.integration_project1_zaroc.view.pages.ruleview.RuleViewPresenter;
import game.integration_project1_zaroc.view.pages.settingsview.SettingsPresenter;
import game.integration_project1_zaroc.view.pages.settingsview.SettingsView;
import game.integration_project1_zaroc.view.pages.winscreenview.WinScreenPresenter;
import game.integration_project1_zaroc.view.pages.winscreenview.WinScreenView;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.pawncolors.PawnColorPaths;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.pawncolors.PawnSideViews;
import game.integration_project1_zaroc.view.pages.ruleview.RuleView;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.profilePictures.ProfilePictures;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    public GameBoardPresenter(GameBoardView view, AppController model) {
        this.view = view;
        this.model = model;
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
                this.model.getMultiplayerService().startTurnPolling(gameId, 0, myUsername);
            }
        }

        updateView();
        addEventHandlers();
        processTurn();
    }

    @Override
    public void update(Object args) {
        System.out.println("Remote zet ontvangen, UI updaten!");
        Platform.runLater(() -> {
            updateView();
            processTurn();
        });
    }

    private void addEventHandlers() {
        view.getSettingsButton().setOnAction(actionEvent -> {
            SettingsView settingsView = new SettingsView(view.getResourceManager());
            new SettingsPresenter(settingsView, this.model);
            Scene settingsScene = new Scene(settingsView);
            settingsScene.setFill(Color.TRANSPARENT);
            Stage settingsStage = new Stage();
            settingsStage.setScene(settingsScene);
            settingsStage.setTitle("Settings");
            settingsStage.initStyle(StageStyle.TRANSPARENT);
            settingsStage.initModality(Modality.APPLICATION_MODAL);
            settingsStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png"))));
            settingsStage.setResizable(false);
            settingsStage.showAndWait();
        });

        view.getInfoButton().setOnAction(event -> {
            RuleView ruleView = new RuleView(view.getResourceManager());
            new RuleViewPresenter(ruleView, this.model);
            Scene ruleScene = new Scene(ruleView);
            ruleScene.setFill(Color.TRANSPARENT);
            Stage ruleStage = new Stage();
            ruleStage.setScene(ruleScene);
            ruleStage.setTitle("Regels");
            ruleStage.initStyle(StageStyle.TRANSPARENT);
            ruleStage.initModality(Modality.APPLICATION_MODAL);
            ruleStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png"))));
            ruleStage.setResizable(false);
            ruleStage.showAndWait();
        });

        view.getUndoButton().setOnAction(e -> {
            undoTimer.stop();
            view.getUndoTimer().setVisible(false);
            view.getSkipButton().setVisible(false);
            view.getSkipButton().setDisable(true);
            view.getUndoButton().setDisable(true);

            model.getGame().undoMove();
            updateView();

            startAfkTimer();
        });
        view.getPlayersPlayingComponent().getPauseButton().setOnAction(event -> {

            pauzeAfkTimer();

            boolean timerWasRunning = (undoTimer != null && undoTimer.getStatus() == Animation.Status.RUNNING);

            player1Animation.pause();
            player2Animation.pause();
            if(timerWasRunning){
                undoTimer.pause();
            }
            PauseScreenView pauseScreenView = new PauseScreenView(view.getResourceManager());
            PauseScreenPresenter pauseScreenPresenter = new PauseScreenPresenter(pauseScreenView,model);

            if (model.getGame().isAllowedSave()){
                pauseScreenView.getNoteUnfinishedGame().setText("NOTE: Current game will be added to Unfinished Games");
            }
            Scene pauseScene = new Scene(pauseScreenView);
            pauseScene.setFill(Color.TRANSPARENT);
            Stage pauseStage = new Stage();
            pauseStage.setScene(pauseScene);
            pauseStage.initOwner(view.getScene().getWindow());
            pauseStage.initStyle(StageStyle.TRANSPARENT);
            pauseStage.initModality(Modality.APPLICATION_MODAL);
            pauseStage.showAndWait();

            if(pauseScreenPresenter.isContinued()){
                if (model.getGame().getCurrentTurn().getCurrentPlayer().getUsername().equals(model.getGame().getParticipation1().getPlayer().getUsername())) {
                    player1Animation.play();
                }
                else{
                    player2Animation.play();
                }

                if(timerWasRunning){
                    undoTimer.play();
                }

                continueAfkTimer();
            }


        });
        view.getSkipButton().setOnAction(event -> {
            this.remainingUndoSeconds = 0;
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
        } else {
            System.out.println("fatal error");
        }

        if (model.getGame().getCurrentTurn().getCurrentPlayer().getUsername().equals(model.getGame().getParticipation1().getPlayer().getUsername())) {
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

        String pfp1 = model.getPlayer1().getProfilePicture();
        String pfp2 = model.getPlayer2().getProfilePicture();
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
    private void showWinner() {
        Platform.runLater(() -> {
            WinScreenView winScreenView = new WinScreenView(this.view.getResourceManager());
            new WinScreenPresenter(winScreenView, model);

            Scene winScene = new Scene(winScreenView);
            winScene.setFill(Color.TRANSPARENT);
            Stage winStage = new Stage();
            winStage.initOwner(view.getScene().getWindow());
            winStage.setTitle("test");
            winStage.initStyle(StageStyle.TRANSPARENT);
            winStage.initModality(Modality.APPLICATION_MODAL);
            winStage.setScene(winScene);
            winStage.showAndWait();
        });
    }

    //----------------------------------------------------------------------------------------------
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~CURRENT PLAYER HANDLE~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //----------------------------------------------------------------------------------------------
    private void processTurn() {
        updateView();

        if (model.getGame().getStatus() == GameStatus.ENDED) {
            System.out.println("Winnaar: " + model.getGame().getWinner().getUsername());
            if (model.getMultiplayerService() != null) {
                model.getMultiplayerService().stopPolling();
            }
            return;
        }

        Player currentPlayer = model.getGame().getCurrentTurn().getCurrentPlayer();

        if (currentPlayer instanceof AIPlayer) {
            stopAfkTimer();
            view.getUndoButton().setDisable(true);
            view.getBoard().getBoard().setDisable(true);
            executeAiLogic((AIPlayer) currentPlayer);
        }
        else if (isRemotePlayer(currentPlayer)) {
            stopAfkTimer();
            view.getUndoButton().setDisable(true);
            view.getBoard().getBoard().setDisable(true);
        }
        else {
            view.getBoard().getBoard().setDisable(false);
            view.getUndoButton().setDisable(true);
            startAfkTimer();
        }
    }

    private boolean isRemotePlayer(Player p) {
        if (!model.isOnlineMultiplayer()) return false;
        if (model.getGame() == null || model.getGame().getGameId() == -1) return false;
        return !p.getUsername().equals(model.getPlayer1().getUsername());
    }

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
    private void executeAiLogic(AIPlayer ai) {
        Thread aiThread = new Thread(() -> {
            Turn bestTurn = ai.decideTurn(model.getGame());

            Platform.runLater(() -> {
                if (bestTurn != null) {
                    executeSingleMove(bestTurn.getFirstMove());
                    updateView();

                    if (model.getGame().getStatus() == GameStatus.ENDED) {
                        showWinner();
                        return;
                    }

                    if (bestTurn.getSecondMove() != null) {
                        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.millis(800));
                        pause.setOnFinished(event -> {
                            executeSingleMove(bestTurn.getSecondMove());
                            updateView();
                            model.getGame().switchCurrentPlayer();

                            if (model.getGame().getStatus() == GameStatus.ENDED) {
                                showWinner();
                                return;
                            }
                            processTurn();
                        });
                        pause.play();
                    } else {
                        model.getGame().switchCurrentPlayer();

                        if (model.getGame().getStatus() == GameStatus.ENDED) {
                            showWinner();
                            return;
                        }
                        processTurn();
                    }
                }
            });
        });
        aiThread.setDaemon(true);
        aiThread.start();
    }

    private void executeSingleMove(Move m) {
        if (m == null) return;
        Peg start = model.getGame().getBoard().getPegPosition(m.getStartPeg().getYPosition(), m.getStartPeg().getXPosition());
        Peg dest = model.getGame().getBoard().getPegPosition(m.getDestinationPeg().getYPosition(), m.getDestinationPeg().getXPosition());

        if (start != null && dest != null && !start.getPawns().isEmpty()) {
            String playerName = model.getGame().getCurrentTurn().getCurrentPlayer().getUsername();
            model.getGame().selectStartPeg(start);
            model.getGame().executeMove(dest);
            System.out.println("Pion geselecteerd op positie: " + m.getStartPeg().getXPosition() + "," + m.getStartPeg().getYPosition());
            System.out.println(playerName);
            System.out.println("Zet uitgevoerd naar: " + m.getDestinationPeg().getXPosition() + "," + m.getDestinationPeg().getYPosition());
        }
    }

    //----------------------------------------------------------------------------------------------
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~HANDLE HUMAN MOVE~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //----------------------------------------------------------------------------------------------
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

                System.out.println("Pion geselecteerd op positie: " + col + "," + row);
                System.out.println(model.getGame().getCurrentTurn().getCurrentPlayer().getUsername());
            }
        }
        else {
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
                System.out.println("Zet uitgevoerd naar: " + col + "," + row);

                updateView();

                if (model.getGame().getStatus() == GameStatus.ENDED) {
                    showWinner();
                    return;
                }
                startUndoTimer();
            } else {
                selectedPawn.setOpacity(1.0);
                System.out.println("Ongeldige zet naar: " + col + "," + row);
            }

            selectedPawn = null;
            clearHighlights();
        }
    }

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
                System.out.println("Voer random zet uit");
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

    private void pauzeAfkTimer(){
        if (afkTimer != null){
            afkTimer.pause();
        }
    }
    private void continueAfkTimer(){
        if (afkTimer!= null){
            afkTimer.play();
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
        view.getSkipButton().setVisible(true);
        view.getSkipButton().setDisable(false);
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

                //debugging
                System.out.println("Timer afgelopen, currentPlayer: " +
                        model.getGame().getCurrentTurn().getCurrentPlayer().getUsername());
                System.out.println("secondMove: " + model.getGame().getCurrentTurn().getSecondMove());

                if (model.getGame().getCurrentTurn().getSecondMove() != null) {
                    model.getGame().switchCurrentPlayer();
                    processTurn();
                }else{
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
}