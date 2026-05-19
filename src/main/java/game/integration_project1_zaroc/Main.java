package game.integration_project1_zaroc;

import game.integration_project1_zaroc.dao.DaoUtils;
import game.integration_project1_zaroc.dao.ZarocDaoException;
import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.view.pages.startview.StartPresenter;
import game.integration_project1_zaroc.view.pages.startview.StartView;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Themes;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        boolean canConnect = canConnectToDatabase();
        Themes theme = Themes.DEFAULT;
        ResourceManager resourceManager = new ResourceManager(theme);
        StartView view = new StartView(resourceManager);
        new StartPresenter(new AppController(canConnect),view);
        Scene scene = new Scene(view);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png"))));
        stage.show();

    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private boolean canConnectToDatabase(){
        try {
            DaoUtils.createTable();
            return true;
        } catch (ZarocDaoException e) {
            return false;
        }
    }
}
