package game.integration_project1_zaroc;

import game.integration_project1_zaroc.core.ResourceManager;
import game.integration_project1_zaroc.core.themes.Themes;
import game.integration_project1_zaroc.view.boardview.GameBoardView;
import game.integration_project1_zaroc.view.ruleview.RuleView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Themes theme = Themes.DEFAULT;
        ResourceManager resourceManager = new ResourceManager(theme);
        GameBoardView view = new GameBoardView(resourceManager);
        //RuleView view = new RuleView(resourceManager); //dit is rule view
        Scene scene = new Scene(view);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/game/integration_project1_zaroc/ui/zaroc.png"))));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
