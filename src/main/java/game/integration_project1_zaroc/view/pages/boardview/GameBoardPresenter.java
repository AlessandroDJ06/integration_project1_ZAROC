package game.integration_project1_zaroc.view.pages.boardview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.view.pages.ruleview.RuleViewPresenter;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.pawncolors.PawnSideViews;
import game.integration_project1_zaroc.view.pages.ruleview.RuleView;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
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

    public GameBoardPresenter(GameBoardView view,AppController model){
        this.view = view;
        this.model = model;
        this.rows = Arrays.asList(view.getPegRowFour(),view.getPegRowThree(),view.getPegRowTwo());
        this.buttons = Arrays.asList(view.getUndoButton(),view.getSettingsButton(),view.getInfoButton());
        updateView();
        addEventHandlers();
    }


    private void addEventHandlers(){
        view.getInfoButton().setOnAction(event -> {
            RuleView ruleView = new RuleView(view.getResourceManager());
            new RuleViewPresenter(ruleView,new AppController());
            Scene ruleScene = new Scene(ruleView);
            ruleScene.setFill(Color.TRANSPARENT);
            Stage ruleStage = new Stage();
            ruleStage.setScene(ruleScene);
            ruleStage.setTitle("Regels");
            ruleStage.initStyle(StageStyle.TRANSPARENT);
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
    }

    private void updateView(){
        view.getPegContainers().get(0).getChildren().addAll(
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.BLACK)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.WHITE)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.BLACK)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.WHITE))
        );

        view.getPegContainers().get(1).getChildren().addAll(
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.WHITE)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.BLACK)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.WHITE)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.BLACK))

        );


        view.getPegContainers().get(2).getChildren().addAll(
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.BLACK)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.WHITE)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.BLACK)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.WHITE))
        );

        view.getPegContainers().get(3).getChildren().addAll(
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.WHITE)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.BLACK)),
                new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.WHITE))

        );

        view.getPegContainers().get(6).getChildren().add(new ImageView(view.getResourceManager().getPawnSideView(PawnSideViews.BLACK)));
        List<ImageView> pawns = Arrays.asList(
                view.getResourceManager().getPawnImageView(PawnColorPaths.BLACK),
                view.getResourceManager().getPawnImageView(PawnColorPaths.WHITE),
                view.getResourceManager().getPawnImageView(PawnColorPaths.BLACK),
                view.getResourceManager().getPawnImageView(PawnColorPaths.WHITE),
                view.getResourceManager().getPawnImageView(PawnColorPaths.BLACK)

        );



        view.getBoard().getBoard().add(pawns.get(0),1,0);
        view.getBoard().getBoard().add(pawns.get(1),3,0);
        view.getBoard().getBoard().add(pawns.get(2),5,0);
        view.getBoard().getBoard().add(pawns.get(3),7,0);
        view.getBoard().getBoard().add(pawns.get(4),5,1);



        for (int i = 0 ; i < pawns.toArray().length ; i++){
            GridPane.setHalignment(pawns.get(i), HPos.CENTER);
            GridPane.setValignment(pawns.get(i), VPos.CENTER);
        }




    }


}
