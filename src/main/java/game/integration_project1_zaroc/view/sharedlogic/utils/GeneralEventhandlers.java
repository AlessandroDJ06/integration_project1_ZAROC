package game.integration_project1_zaroc.view.sharedlogic.utils;

import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

public class GeneralEventhandlers {

    public static void addHoverEffect(Button button){
        button.setOnMouseEntered(event -> {
            button.setScaleX(1.2);
            button.setScaleY(1.2);
        });

        button.setOnMouseExited(event -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });

    }

    public static void addSoundEffect(Button button, ResourceManager resourceManager){
        button.addEventHandler(ActionEvent.ACTION, event -> {
            resourceManager.getSfxManager().playButtonPress();
        });
    }
}
