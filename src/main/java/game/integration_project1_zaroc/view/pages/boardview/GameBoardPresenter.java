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
                handlePegClick(col, row);
            });
        }
    }

    private void updateView(){
        if (model.getGame() != null){
            view.getPlayersPlayingComponent().setFirstPlayer(
                    model.getGame().getParticipation1().getPlayer().getUsername()

            );

            System.out.println(model.getGame().getParticipation1().getPlayer().getUsername());

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
                PawnColorPaths kleur;
                if ((i + laag) % 2 == 0) {
                    kleur = colorPlayerOne;
                } else {
                    kleur = colorPlayerTwo;
                }

                ImageView pion = view.getResourceManager().getPawnImageView(kleur);
                pion.setMouseTransparent(true);
                GridPane.setHalignment(pion, HPos.CENTER);
                GridPane.setValignment(pion, VPos.CENTER);
                pion.setTranslateY(-7 * laag);
                view.getBoard().getBoard().add(pion, col, row);
            }
        }

    }

    private void handlePegClick(int col, int row) {
        if (selectedPawn == null) {
            ImageView bovenste = getBovenstePion(col, row);

            if (bovenste != null && getAantalPionnenInCel(col,row) >1) {
                selectedPawn = bovenste;
                selectedPawn.setOpacity(0.5);
                System.out.println("Pion opgepakt van " + col + "," + row);
            }
        }
        else {
            view.getBoard().getBoard().getChildren().remove(selectedPawn);

            int aantalOpDoel = getAantalPionnenInCel(col, row);
            selectedPawn.setTranslateY(-4 * aantalOpDoel);
            view.getBoard().getBoard().add(selectedPawn, col, row);

            selectedPawn.setOpacity(1.0);
            selectedPawn = null;
            System.out.println("Pion neergezet op " + col + "," + row);
        }
    }

    private ImageView getBovenstePion(int col, int row) {
        ImageView bovenste = null;
        for (Node node : view.getBoard().getBoard().getChildren()) {
            if (node instanceof ImageView && isOpPositie(node, col, row)) {
                bovenste = (ImageView) node;
            }
        }
        return bovenste;
    }

    private int getAantalPionnenInCel(int col, int row) {
        int count = 0;
        for (Node node : view.getBoard().getBoard().getChildren()) {
            if (node instanceof ImageView && isOpPositie(node, col, row)) {
                count++;
            }
        }
        return count;
    }

    private boolean isOpPositie(Node node, int col, int row) {
        Integer c = GridPane.getColumnIndex(node);
        Integer r = GridPane.getRowIndex(node);
        return c != null && r != null && c == col && r == row;
    }


}
