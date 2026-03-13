package game.integration_project1_zaroc.view.pages.boardview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.view.pages.ruleview.RuleViewPresenter;
import game.integration_project1_zaroc.view.pages.settingsview.SettingsPresenter;
import game.integration_project1_zaroc.view.pages.settingsview.SettingsView;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.pawncolors.PawnColorPaths;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.pawncolors.PawnSideViews;
import game.integration_project1_zaroc.view.pages.ruleview.RuleView;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GameBoardPresenter {
    private GameBoardView view;
    private AppController model;
    private List<HBox> rows;
    private List<Button> buttons;

    private PawnSideViews sideViewPlayer1;
    private PawnSideViews sideViewPlayer2;

    private PawnColorPaths colorPlayerOne;
    private PawnColorPaths colorPlayerTwo;

    private ImageView selectedPawn;
    private ImageView sideViewOfSelectedPawn;
    private VBox parentOfSideView;

    public GameBoardPresenter(GameBoardView view,AppController model){
        this.view = view;
        this.model = model;
        this.rows = Arrays.asList(view.getPegRowFour(),view.getPegRowThree(),view.getPegRowTwo());
        this.buttons = Arrays.asList(view.getUndoButton(),view.getSettingsButton(),view.getInfoButton());

        this.sideViewPlayer1 = PawnSideViews.values()[model.getGame().getParticipation1().getPawnColor().ordinal()];
        this.sideViewPlayer2 = PawnSideViews.values()[model.getGame().getParticipation2().getPawnColor().ordinal()];

        this.colorPlayerOne =  PawnColorPaths.values()[model.getGame().getParticipation1().getPawnColor().ordinal()];
        this.colorPlayerTwo = PawnColorPaths.values()[model.getGame().getParticipation2().getPawnColor().ordinal()];

        this.selectedPawn = null;
        this.sideViewOfSelectedPawn = null;
        this.parentOfSideView = null;
        updateView();
        addEventHandlers();
    }


    private void addEventHandlers(){
        view.getSettingsButton().setOnAction(actionEvent -> {
            SettingsView settingsView = new SettingsView(view.getResourceManager());
            new SettingsPresenter(settingsView,new AppController());
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
            new RuleViewPresenter(ruleView,new AppController());
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

        for (Button button : buttons){
            GeneralEventhandlers.addHoverEffect(button);
        }

        for (HBox row : rows){
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

        for (ImageView position : view.getBoard().getPegPositions()){
            position.setOnMouseEntered(mouseEvent -> {
                position.setOpacity(0.5);
            });
            position.setOnMouseExited(mouseEvent -> {
                position.setOpacity(1);

            });

            position.setOnMouseClicked(event -> {
                int col = GridPane.getColumnIndex(position);
                int row = GridPane.getRowIndex(position);
                System.out.println(view.getBoard().getPegPositions().indexOf(position));
                handlePegClick(col, row,view.getBoard().getPegPositions().indexOf(position));
            });
        }
    }

    private void updateView(){
        if (model.getGame() != null){
            view.getPlayersPlayingComponent().setFirstPlayer(
                    model.getGame().getParticipation1().getPlayer().getUsername()

            );

            view.getPlayersPlayingComponent().setSecondPlayer(
                    model.getGame().getParticipation2().getPlayer().getUsername()
            );
        } else {
            System.out.println("fatal error");
        }

        int pionnenPerContainer = 4;

        for (int c = 0; c < pionnenPerContainer; c++) {
            var container = view.getPegContainers().get(c);
            container.getChildren().clear();

            for (int i = 0; i < pionnenPerContainer; i++) {
                PawnSideViews sideColor;
                if ((c + i) % 2 == 0) {
                    sideColor = sideViewPlayer2;
                } else {
                    sideColor = sideViewPlayer1;
                }

                ImageView sidePawn = new ImageView(view.getResourceManager().getPawnSideView(sideColor));
                container.getChildren().add(sidePawn);
            }
        }

        // De 4 locaties (kolommen) waar de stapels van 4 moeten komen
        int[] kolommen = {1, 3, 5, 7};
        int row = 0; // De rij waar deze pegs staan

        for (int i = 0; i < kolommen.length; i++) {
            int col = kolommen[i];

            for (int laag = 0; laag < 4; laag++) {
                PawnColorPaths color;
                if ((i + laag) % 2 == 0) {
                    color = colorPlayerOne;
                } else {
                    color = colorPlayerTwo;
                }

                ImageView pawn = view.getResourceManager().getPawnImageView(color);
                pawn.setMouseTransparent(true);
                GridPane.setHalignment(pawn, HPos.CENTER);
                GridPane.setValignment(pawn, VPos.CENTER);
                pawn.setTranslateY(-5 * laag);
                view.getBoard().getBoard().add(pawn, col, row);
            }
        }

    }

    private void handlePegClick(int col, int row , int index) {
        if (selectedPawn == null) {
            ImageView bovenste = getTopPawn(col, row);

            if (bovenste != null && getAmountInEachCell(col,row) >1) {
                selectedPawn = bovenste;
                if (index < 13){
                    parentOfSideView = view.getPegContainers().get(index);
                    sideViewOfSelectedPawn = ((ImageView) parentOfSideView.getChildren().getFirst());
                }
                selectedPawn.setOpacity(0.5);
                sideViewOfSelectedPawn.setOpacity(0.5);
                System.out.println("Pion opgepakt van " + col + "," + row);
            }
        }
        else {
            view.getBoard().getBoard().getChildren().remove(selectedPawn);

            int aantalOpDoel = getAmountInEachCell(col, row);
            selectedPawn.setTranslateY(-5 * aantalOpDoel);
            view.getBoard().getBoard().add(selectedPawn, col, row);
            if (index < 13) {
                view.getPegContainers().get(index).getChildren().addFirst(sideViewOfSelectedPawn);
            }  else {
                parentOfSideView.getChildren().remove(sideViewOfSelectedPawn);
            }


            selectedPawn.setOpacity(1.0);
            sideViewOfSelectedPawn.setOpacity(1.0);
            selectedPawn = null;
            sideViewOfSelectedPawn = null;
            System.out.println("Pion neergezet op " + col + "," + row);
        }
    }

    private ImageView getTopPawn(int col, int row) {
        ImageView bovenste = null;
        for (Node node : view.getBoard().getBoard().getChildren()) {
            if (node instanceof ImageView && isAtPosition(node, col, row)) {
                bovenste = (ImageView) node;
            }
        }
        return bovenste;
    }

    private int getAmountInEachCell(int col, int row) {
        int count = 0;
        for (Node node : view.getBoard().getBoard().getChildren()) {
            if (node instanceof ImageView && isAtPosition(node, col, row)) {
                count++;
            }
        }
        return count;
    }

    private boolean isAtPosition(Node node, int col, int row) {
        Integer c = GridPane.getColumnIndex(node);
        Integer r = GridPane.getRowIndex(node);
        return c != null && r != null && c == col && r == row;
    }


}
