package game.integration_project1_zaroc.view.pages.startview;

import game.integration_project1_zaroc.model.AppController;
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

        });


    }
}
