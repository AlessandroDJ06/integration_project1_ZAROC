package game.integration_project1_zaroc.view.sharedlogic;

import javafx.scene.Node;
import javafx.stage.Stage;

public class NavigationService {
    private void createStage(){

    }

    public static void closeWindow(Node view) {
        Stage stage = (Stage) view.getScene().getWindow();
        stage.close();
    }
}
