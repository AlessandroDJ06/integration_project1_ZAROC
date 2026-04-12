package game.integration_project1_zaroc.view.pages.selectgamemodeview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.view.pages.boardview.GameBoardPresenter;
import game.integration_project1_zaroc.view.pages.boardview.GameBoardView;
import game.integration_project1_zaroc.view.pages.gamesetupview.GameSetupPresenter;
import game.integration_project1_zaroc.view.pages.gamesetupview.GameSetupView;
import game.integration_project1_zaroc.view.pages.playervsaiview.PlayerVsAiPresenter;
import game.integration_project1_zaroc.view.pages.playervsaiview.PlayerVsAiView;
import game.integration_project1_zaroc.view.pages.ruleview.RuleView;
import game.integration_project1_zaroc.view.pages.ruleview.RuleViewPresenter;
import game.integration_project1_zaroc.view.pages.settingsview.SettingsPresenter;
import game.integration_project1_zaroc.view.pages.settingsview.SettingsView;
import game.integration_project1_zaroc.view.pages.unfinishedgamesview.UnfinishedGamesPresenter;
import game.integration_project1_zaroc.view.pages.unfinishedgamesview.UnfinishedGamesView;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class SelectGamemodePresenter {
    private SelectGamemodeView view;
    private AppController model;
    private List<Button> buttons;

    public SelectGamemodePresenter(AppController model , SelectGamemodeView view){
        this.view = view;
        this.model = model;
        this.buttons = Arrays.asList(
                view.getInfoButton(),
                view.getSettingsButton(),
                view.getProfileButton(),
                view.getPlayerVsAiButton(),
                view.getPlayerVsPlayerButton(),
                view.getUnfinishedGamesButton());
        addEventHandlers();
    }

    private void addEventHandlers(){
        for (Button button : buttons){
            GeneralEventhandlers.addHoverEffect(button);
        }

        view.getSettingsButton().setOnAction(actionEvent -> {
            SettingsView settingsView = new SettingsView(view.getResourceManager());
            new SettingsPresenter(settingsView,this.model);
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
            new RuleViewPresenter(ruleView,this.model);
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

        view.getPlayerVsAiButton().setOnAction(event -> {
            PlayerVsAiView playerVsAiView = new PlayerVsAiView(view.getResourceManager());
            new PlayerVsAiPresenter(playerVsAiView,model);
            Scene playerVsAiScene = new Scene(playerVsAiView,900,750);
            playerVsAiScene.setFill(Color.TRANSPARENT);
            Stage playerVsAiStage = new Stage();
            playerVsAiStage.setScene(playerVsAiScene);
            playerVsAiStage.setTitle("Selecteer tegenstander");
            playerVsAiStage.initStyle(StageStyle.TRANSPARENT);
            playerVsAiStage.initModality(Modality.APPLICATION_MODAL);
            playerVsAiStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png"))));
            playerVsAiStage.setResizable(false);
            playerVsAiStage.showAndWait();
            if (model.getPlayer2() != null){
                navigateToGameSetup();
            }
        });

        view.getUnfinishedGamesButton().setOnAction(event -> {
            UnfinishedGamesView unfinishedView = new UnfinishedGamesView(view.getResourceManager());
            new UnfinishedGamesPresenter(unfinishedView, model);
            Scene unfinishedScene = new Scene(unfinishedView, 900, 750);
            unfinishedScene.setFill(Color.TRANSPARENT);
            Stage unfinishedStage = new Stage();
            unfinishedStage.setScene(unfinishedScene);
            unfinishedStage.setTitle("Hervat een spel");
            unfinishedStage.initStyle(StageStyle.TRANSPARENT);
            unfinishedStage.initModality(Modality.APPLICATION_MODAL);
            unfinishedStage.getIcons().add(new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png")
            )));
            unfinishedStage.setResizable(false);
            unfinishedStage.showAndWait();
            if (model.getGame() != null ){
                navigateToGame();
            }

        });

    }

    private void navigateToGameSetup() {
        GameSetupView gameSetupView = new GameSetupView(view.getResourceManager());
        new GameSetupPresenter(gameSetupView,model);
        view.getScene().setRoot(gameSetupView);
    }

    private void navigateToGame(){
        GameBoardView gameBoardView = new GameBoardView(view.getResourceManager());
        new GameBoardPresenter(gameBoardView,model);
        view.getScene().setRoot(gameBoardView);
    }
}
