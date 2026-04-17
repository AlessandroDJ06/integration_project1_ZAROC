package game.integration_project1_zaroc.view.pages.settingsview;

import game.integration_project1_zaroc.model.AppController;
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
        view.getVolumeSlider().setValue(view.getResourceManager().getMusicManager().getVolume()*10);
        view.getSoundSlider().setValue(view.getResourceManager().getSfxManager().getVolume()*10);
    }

    private void addEventHandlers(){
        view.getVolumeSlider().valueProperty().addListener((observable, oldValue, newValue) -> {
            setVolumeMusicSlider();
            setVolumeIcon();
        });

        view.getSoundSlider().valueProperty().addListener((observable, oldValue, newValue) -> {
            setVolumeSoundSlider();
            setSoundIcon();
        });


        view.getReturnButton().setOnAction(actionEvent ->{
            Stage stage = (Stage) view.getScene().getWindow();
            stage.close();
        });
        GeneralEventhandlers.addHoverEffect(view.getReturnButton());
        GeneralEventhandlers.addSoundEffect(view.getReturnButton(),view.getResourceManager());


    }

    private void setVolumeMusicSlider() {
        view.getResourceManager().getMusicManager().setVolume(view.getVolumeSlider().getValue());
    }

    private void setVolumeSoundSlider() {
        view.getResourceManager().getSfxManager().setVolume(view.getSoundSlider().getValue());
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
