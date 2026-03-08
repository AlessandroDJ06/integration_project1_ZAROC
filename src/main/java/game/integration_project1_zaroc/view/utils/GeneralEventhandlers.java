package game.integration_project1_zaroc.view.utils;

import javafx.scene.control.Button;

public class GeneralEventhandlers {

    public void addHoverEffect(Button button){
        button.setOnMouseEntered(event -> {
            button.setScaleX(1.2);
            button.setScaleY(1.2);
        });

        button.setOnMouseExited(event -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
    }
}
