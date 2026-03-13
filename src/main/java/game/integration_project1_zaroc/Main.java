package game.integration_project1_zaroc;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.view.pages.boardview.GameBoardPresenter;
import game.integration_project1_zaroc.view.pages.gamesetupview.GameSetupPresenter;
import game.integration_project1_zaroc.view.pages.gamesetupview.GameSetupView;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Themes;
import game.integration_project1_zaroc.view.pages.boardview.GameBoardView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Themes theme = Themes.DONKEY_KONG;
        ResourceManager resourceManager = new ResourceManager(theme);
        //GameBoardView view = new GameBoardView(resourceManager);
        //new GameBoardPresenter(view,new AppController());
        GameSetupView view = new GameSetupView(resourceManager);
        new GameSetupPresenter(view,new AppController());
        Scene scene = new Scene(view);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png"))));
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
