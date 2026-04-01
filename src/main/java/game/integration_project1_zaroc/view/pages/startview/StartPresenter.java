package game.integration_project1_zaroc.view.pages.startview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.view.pages.createaccountview.CreateAccountPresenter;
import game.integration_project1_zaroc.view.pages.createaccountview.CreateAccountView;
import game.integration_project1_zaroc.view.pages.gamesetupview.GameSetupPresenter;
import game.integration_project1_zaroc.view.pages.gamesetupview.GameSetupView;
import game.integration_project1_zaroc.view.pages.leaderboardview.LeaderboardPresenter;
import game.integration_project1_zaroc.view.pages.leaderboardview.LeaderboardView;
import game.integration_project1_zaroc.view.pages.loginview.LoginPresenter;
import game.integration_project1_zaroc.view.pages.loginview.LoginView;
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

public class StartPresenter {
    private AppController model;
    private StartView view;
    private List<Button> buttons;
    private Stage loginStage;
    private Stage createAccountStage;

    public StartPresenter(AppController model , StartView view){
        this.model = model;
        this.view = view;
        this.buttons = Arrays.asList(
                view.getInfoButton(),
                view.getSettingsButton(),
                view.getLeaderboardButton(),
                view.getCreateAccountButton(),
                view.getLoginButton()
        );
        addEventHandlers();
    }

    private void addEventHandlers(){
        for (Button button : buttons){
            GeneralEventhandlers.addHoverEffect(button);
        }

        view.getLoginButton().setOnAction(event -> {
            LoginView loginView = new LoginView(this.view.getResourceManager());
            new LoginPresenter(this.model, loginView);
            Scene loginScene = new Scene(loginView);
            loginScene.setFill(Color.TRANSPARENT);
            this.loginStage = new Stage();
            loginStage.setScene(loginScene);
            loginStage.setTitle("login");
            loginStage.initStyle(StageStyle.TRANSPARENT);
            loginStage.initModality(Modality.APPLICATION_MODAL);
            loginStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png"))));
            loginStage.setResizable(false);
            loginStage.showAndWait();
            if (model.isLoggedIn()) {
                navigateToGameSetup();
            }
        });

        view.getCreateAccountButton().setOnAction(event -> {
            CreateAccountView createAccountView = new CreateAccountView(this.view.getResourceManager());
            new CreateAccountPresenter(this.model, createAccountView);
            Scene createAccountScene = new Scene(createAccountView);
            createAccountScene.setFill(Color.TRANSPARENT);
            this.createAccountStage = new Stage();
            createAccountStage.setScene(createAccountScene);
            createAccountStage.setTitle("createAccount");
            createAccountStage.initStyle(StageStyle.TRANSPARENT);
            createAccountStage.initModality(Modality.APPLICATION_MODAL);
            createAccountStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png"))));
            createAccountStage.setResizable(false);
            createAccountStage.showAndWait();
            if (model.isLoggedIn()) {
                navigateToGameSetup();
            }
        });

        view.getLeaderboardButton().setOnAction(actionEvent -> {
            LeaderboardView leaderboardView = new LeaderboardView(view.getResourceManager());
            new LeaderboardPresenter(leaderboardView,this.model);
            Scene leaderboardScene = new Scene(leaderboardView);
            leaderboardScene.setFill(Color.TRANSPARENT);
            Stage leaderboardStage = new Stage();
            leaderboardStage.setScene(leaderboardScene);
            leaderboardStage.setTitle("Leaderboard");
            leaderboardStage.initStyle(StageStyle.TRANSPARENT);
            leaderboardStage.initModality(Modality.APPLICATION_MODAL);
            leaderboardStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png"))));
            leaderboardStage.setResizable(false);
            leaderboardStage.showAndWait();

        });



    }
    private void navigateToGameSetup() {
        GameSetupView setupView = new GameSetupView(view.getResourceManager());
        new GameSetupPresenter(setupView, model);
        view.getScene().setRoot(setupView);
    }
}
