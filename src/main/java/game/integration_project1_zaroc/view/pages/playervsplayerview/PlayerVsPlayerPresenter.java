package game.integration_project1_zaroc.view.pages.playervsplayerview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.view.pages.createaccountview.CreateAccountPresenter;
import game.integration_project1_zaroc.view.pages.createaccountview.CreateAccountView;
import game.integration_project1_zaroc.view.pages.loginview.LoginPresenter;
import game.integration_project1_zaroc.view.pages.loginview.LoginView;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.profilePictures.ProfilePictures;
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

public class PlayerVsPlayerPresenter {
    private PlayerVsPlayerView view;
    private AppController model;

    public PlayerVsPlayerPresenter(PlayerVsPlayerView view, AppController model) {
        this.view = view;
        this.model = model;
        addEventHandlers();
        updateView();
    }

    private void addEventHandlers(){
        for (Button button : Arrays.asList(view.getReturnButton(), view.getCreateAccountPlayerTwo(), view.getLoginPlayerTwo(), view.getStartGame())){
            GeneralEventhandlers.addHoverEffect(button);
        }
        view.getReturnButton().setOnAction(e -> {
            model.setPlayer2(null);
            closeWindow();
        });

        view.getStartGame().setOnAction(event -> {
            closeWindow();
        });

        view.getMultiplayerButton().setOnAction(event -> {
            model.setOnlineMultiplayer(true);
            closeWindow();
        });

        view.getLoginPlayerTwo().setOnAction(event -> {
            LoginView loginView = new LoginView(this.view.getResourceManager());
            new LoginPresenter(this.model, loginView,false);
            Scene loginScene = new Scene(loginView);
            loginScene.setFill(Color.TRANSPARENT);
            Stage loginStage = new Stage();
            loginStage.setScene(loginScene);
            loginStage.setTitle("login");
            loginStage.initStyle(StageStyle.TRANSPARENT);
            loginStage.initModality(Modality.APPLICATION_MODAL);
            loginStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png"))));
            loginStage.setResizable(false);
            loginStage.showAndWait();
            if (model.getPlayer2() != null) {
                updateView();
            }
        });

        view.getCreateAccountPlayerTwo().setOnAction(event -> {
            CreateAccountView createAccountView = new CreateAccountView(this.view.getResourceManager());
            new CreateAccountPresenter(this.model, createAccountView,false);
            Scene createAccountScene = new Scene(createAccountView);
            createAccountScene.setFill(Color.TRANSPARENT);
            Stage createAccountStage = new Stage();
            createAccountStage.setScene(createAccountScene);
            createAccountStage.setTitle("createAccount");
            createAccountStage.initStyle(StageStyle.TRANSPARENT);
            createAccountStage.initModality(Modality.APPLICATION_MODAL);
            createAccountStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png"))));
            createAccountStage.setResizable(false);
            createAccountStage.showAndWait();
            if (model.getPlayer2() != null) {
                updateView();
            }
        });

    }

    private void updateView(){
        view.getPlayerOneName().setText(model.getPlayer1().getUsername());
        view.getPlayerOnePfp().setImage(view.getResourceManager().getProfilePicture(ProfilePictures.valueOf(model.getPlayer1().getProfilePicture())));

        if (model.getPlayer2() != null){
            view.getPlayerTwoName().setText(model.getPlayer2().getUsername());
            view.getPlayerTwoPfp().setImage(view.getResourceManager().getProfilePicture(ProfilePictures.valueOf(model.getPlayer2().getProfilePicture())));

            view.getContent().getChildren().remove(view.getLoginButtons());
            view.getContent().getChildren().add(view.getPlayerTwoInfo());
        }
    }

    private void closeWindow(){
        Stage stage = (Stage) view.getScene().getWindow();
        stage.close();
    }
}
