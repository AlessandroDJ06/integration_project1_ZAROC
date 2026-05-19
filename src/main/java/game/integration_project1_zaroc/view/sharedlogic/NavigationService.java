package game.integration_project1_zaroc.view.sharedlogic;

import game.integration_project1_zaroc.dao.UnfinishedGame;
import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.view.pages.boardview.GameBoardPresenter;
import game.integration_project1_zaroc.view.pages.boardview.GameBoardView;
import game.integration_project1_zaroc.view.pages.createaccountview.CreateAccountPresenter;
import game.integration_project1_zaroc.view.pages.createaccountview.CreateAccountView;
import game.integration_project1_zaroc.view.pages.gamesetupview.GameSetupPresenter;
import game.integration_project1_zaroc.view.pages.gamesetupview.GameSetupView;
import game.integration_project1_zaroc.view.pages.leaderboardview.LeaderboardPresenter;
import game.integration_project1_zaroc.view.pages.leaderboardview.LeaderboardView;
import game.integration_project1_zaroc.view.pages.loginview.LoginPresenter;
import game.integration_project1_zaroc.view.pages.loginview.LoginView;
import game.integration_project1_zaroc.view.pages.multiplayerguestview.MultiPlayerGuestPresenter;
import game.integration_project1_zaroc.view.pages.multiplayerguestview.MultiPlayerGuestView;
import game.integration_project1_zaroc.view.pages.multiplayerhostview.MultiPlayerHostPresenter;
import game.integration_project1_zaroc.view.pages.multiplayerhostview.MultiPlayerHostView;
import game.integration_project1_zaroc.view.pages.multiplayersetup.MultiplayerSetupPresenter;
import game.integration_project1_zaroc.view.pages.multiplayersetup.MultiplayerSetupView;
import game.integration_project1_zaroc.view.pages.playervsaiview.PlayerVsAiPresenter;
import game.integration_project1_zaroc.view.pages.playervsaiview.PlayerVsAiView;
import game.integration_project1_zaroc.view.pages.playervsplayerview.PlayerVsPlayerPresenter;
import game.integration_project1_zaroc.view.pages.playervsplayerview.PlayerVsPlayerView;
import game.integration_project1_zaroc.view.pages.ruleview.RuleView;
import game.integration_project1_zaroc.view.pages.ruleview.RuleViewPresenter;
import game.integration_project1_zaroc.view.pages.selectgamemodeview.SelectGamemodePresenter;
import game.integration_project1_zaroc.view.pages.selectgamemodeview.SelectGamemodeView;
import game.integration_project1_zaroc.view.pages.settingsview.SettingsPresenter;
import game.integration_project1_zaroc.view.pages.settingsview.SettingsView;
import game.integration_project1_zaroc.view.pages.statisticsview.StatisticsPresenter;
import game.integration_project1_zaroc.view.pages.statisticsview.StatisticsView;
import game.integration_project1_zaroc.view.pages.unfinishedgamesview.UnfinishedGamesPresenter;
import game.integration_project1_zaroc.view.pages.unfinishedgamesview.UnfinishedGamesView;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class NavigationService {

    private static Stage createStage(String title) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(new Image(Objects.requireNonNull(NavigationService.class.getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png"))));
        stage.setResizable(false);
        return stage;
    }

    private static Stage getMainStage() {
        return (Stage) Stage.getWindows().getFirst();
    }

    public static void navigateToGameModeSelection(ResourceManager resourceManager, AppController model) {
        SelectGamemodeView gamemodeView = new SelectGamemodeView(resourceManager);
        new SelectGamemodePresenter(model, gamemodeView);
        getMainStage().getScene().setRoot(gamemodeView);
    }
/**Handels the initialization and navigation to GameBoardView/presenter.
 * ClosHandler is attached after initialization.
 * */
    public static void navigateToGameBoard(ResourceManager resourceManager, AppController model) {
        GameBoardView gameBoardView = new GameBoardView(resourceManager);
        GameBoardPresenter gameBoardPresenter = new GameBoardPresenter(gameBoardView, model);
        getMainStage().getScene().setRoot(gameBoardView);
        gameBoardPresenter.attachCloseHandler(getMainStage());
    }

    public static void navigateToGameSetup(ResourceManager resourceManager, AppController model) {
        GameSetupView gameSetupView = new GameSetupView(resourceManager);
        new GameSetupPresenter(gameSetupView, model);
        getMainStage().getScene().setRoot(gameSetupView);
    }

    public static Stage navigateToRules(ResourceManager resourceManager, AppController model) {
        Stage stage = createStage("rules");
        RuleView ruleView = new RuleView(resourceManager);
        new RuleViewPresenter(ruleView, model);
        Scene ruleScene = new Scene(ruleView);
        ruleScene.setFill(Color.TRANSPARENT);
        stage.setScene(ruleScene);
        return stage;
    }

    public static Stage navigateToSettings(ResourceManager resourceManager, AppController model) {
        Stage stage = createStage("settings");
        SettingsView settingsView = new SettingsView(resourceManager);
        new SettingsPresenter(settingsView, model);
        Scene settingsScene = new Scene(settingsView);
        settingsScene.setFill(Color.TRANSPARENT);
        stage.setScene(settingsScene);
        return stage;
    }

    public static Stage navigateToLeaderboard(ResourceManager resourceManager, AppController model) {
        Stage stage = createStage("leaderboard");
        LeaderboardView leaderboardView = new LeaderboardView(resourceManager);
        new LeaderboardPresenter(leaderboardView, model);
        Scene leaderboardScene = new Scene(leaderboardView);
        leaderboardScene.setFill(Color.TRANSPARENT);
        stage.setScene(leaderboardScene);
        return stage;
    }

    public static Stage navigateToLoginView(ResourceManager resourceManager, AppController model, boolean isPlayerOne) {
        Stage stage = createStage("login");
        LoginView loginView = new LoginView(resourceManager);
        new LoginPresenter(model, loginView, isPlayerOne);
        Scene loginScene = new Scene(loginView);
        loginScene.setFill(Color.TRANSPARENT);
        stage.setScene(loginScene);
        return stage;
    }

    public static Stage navigateToCreateAccountView(ResourceManager resourceManager, AppController model, boolean isPlayerOne) {
        Stage stage = createStage("create account");
        CreateAccountView createAccountView = new CreateAccountView(resourceManager);
        new CreateAccountPresenter(model, createAccountView, isPlayerOne);
        Scene createAccountScene = new Scene(createAccountView);
        createAccountScene.setFill(Color.TRANSPARENT);
        stage.setScene(createAccountScene);
        return stage;
    }

    public static Stage navigateToUnfinishedGames(ResourceManager resourceManager, AppController model) {
        Stage stage = createStage("unfinished games");
        UnfinishedGamesView unfinishedView = new UnfinishedGamesView(resourceManager);
        new UnfinishedGamesPresenter(unfinishedView, model);
        Scene unfinishedScene = new Scene(unfinishedView, 900, 750);
        stage.setScene(unfinishedScene);
        return stage;
    }

    public static Stage navigateToPlayerVsAiView(ResourceManager resourceManager, AppController model) {
        Stage stage = createStage("player vs ai");
        PlayerVsAiView playerVsAiView = new PlayerVsAiView(resourceManager);
        new PlayerVsAiPresenter(playerVsAiView, model);
        Scene playerVsAiScene = new Scene(playerVsAiView, 900, 750);
        playerVsAiScene.setFill(Color.TRANSPARENT);
        stage.setScene(playerVsAiScene);
        return stage;
    }

    public static Stage navigateToPlayerVsPlayerView(ResourceManager resourceManager, AppController model, UnfinishedGame playerTwo) {
        Stage stage = createStage("player vs player");
        PlayerVsPlayerView playerVsPlayerView = new PlayerVsPlayerView(resourceManager);
        new PlayerVsPlayerPresenter(playerVsPlayerView, model, playerTwo);
        Scene playerVsPlayerScene = new Scene(playerVsPlayerView, 900, 750);
        playerVsPlayerScene.setFill(Color.TRANSPARENT);
        stage.setScene(playerVsPlayerScene);
        return stage;
    }

    public static Stage navigateToMultiplayerView(ResourceManager resourceManager, AppController model) {
        Stage stage = createStage("multiplayer");
        MultiplayerSetupView multiplayerSetupView = new MultiplayerSetupView(resourceManager);
        new MultiplayerSetupPresenter(multiplayerSetupView, model);
        Scene multiplayerScene = new Scene(multiplayerSetupView);
        multiplayerScene.setFill(Color.TRANSPARENT);
        stage.setScene(multiplayerScene);
        return stage;
    }

    public static Stage navigateToStatisticsView(ResourceManager resourceManager, AppController model) {
        Stage stage = createStage("statistics");
        StatisticsView statisticsView = new StatisticsView(resourceManager);
        new StatisticsPresenter(statisticsView, model);
        Scene statsScene = new Scene(statisticsView);
        statsScene.setFill(Color.TRANSPARENT);
        stage.setScene(statsScene);
        return stage;
    }

    public static Stage navigateToMultiplayerHostView(ResourceManager resourceManager, AppController model, int existingGameId) {
        Stage stage = createStage("multiplayer host");
        MultiPlayerHostView multiPlayerHostView = new MultiPlayerHostView(resourceManager);
        new MultiPlayerHostPresenter(multiPlayerHostView, model, existingGameId);
        Scene hostScene = new Scene(multiPlayerHostView, 900, 750);
        hostScene.setFill(Color.TRANSPARENT);
        stage.setScene(hostScene);
        return stage;
    }

    public static Stage navigateToMultiplayerGuestView(ResourceManager resourceManager, AppController model) {
        Stage stage = createStage("multiplayer guest");
        MultiPlayerGuestView multiPlayerGuestView = new MultiPlayerGuestView(resourceManager);
        new MultiPlayerGuestPresenter(multiPlayerGuestView, model);
        Scene guestScene = new Scene(multiPlayerGuestView, 900, 750);
        guestScene.setFill(Color.TRANSPARENT);
        stage.setScene(guestScene);
        return stage;
    }

    public static void closeWindow(Node view) {
        Stage stage = (Stage) view.getScene().getWindow();
        stage.close();
    }
}