package game.integration_project1_zaroc.view.pages.settingsview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.view.pages.ruleview.RuleView;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SettingsPresenter {

        private final SettingsView view;
        private final AppController model;

        public SettingsPresenter(SettingsView View, AppController model) {
            this.view = View;
            this.model = model;
            addEventHandlers();

        }

        private void addEventHandlers(){
            view.getReturnButton().setOnAction(actionEvent ->{
                Stage stage = (Stage) view.getScene().getWindow();
                stage.close();
            });
            GeneralEventhandlers.addHoverEffect(view.getReturnButton());

            view.getVolumeSlider().setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    setVolumeIcon();
                }
            });

            view.getVolumeSlider().setOnMouseClicked(event -> {
               setVolumeIcon();
            });
            view.getSoundSlider().setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    setSoundIcon();
                }
            });
            view.getSoundSlider().setOnMouseClicked(event -> {
                setSoundIcon();
            });



        }
    private void setVolumeIcon() {
        double currentValue = view.getVolumeSlider().getValue();
        if(currentValue==0){
            view.setVolumeIcon(new Image(getClass().getResource("/game/integration_project1_zaroc/ui/icons/mute_music.png").toExternalForm()));
        }else{
            view.setVolumeIcon(new Image(getClass().getResource("/game/integration_project1_zaroc/ui/icons/music.png").toExternalForm()));
        }
    }
    private void setSoundIcon() {
        double currentValue = view.getSoundSlider().getValue();
        if(currentValue==0){
            view.setSoundIcon(new Image(getClass().getResource("/game/integration_project1_zaroc/ui/icons/mute_sound.png").toExternalForm()));
        }else{
            view.setSoundIcon(new Image(getClass().getResource("/game/integration_project1_zaroc/ui/icons/sound.png").toExternalForm()));
        }
    }
}
