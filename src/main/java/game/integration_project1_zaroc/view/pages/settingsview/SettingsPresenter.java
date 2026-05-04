package game.integration_project1_zaroc.view.pages.settingsview;


import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.model.selectionslider.ThemePickerModel;
import game.integration_project1_zaroc.utils.Observer;
import game.integration_project1_zaroc.view.pages.settingsview.SettingsView;
import game.integration_project1_zaroc.view.sharedlogic.NavigationService;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Themes;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class SettingsPresenter implements Observer {

    private SettingsView view;
    private AppController model;
    private ThemePickerModel themePickerModel;

    public SettingsPresenter(SettingsView view, AppController model) {
        this.view = view;
        this.model = model;
        view.getResourceManager().addObserver(this);
        this.themePickerModel = model.getThemePickerModel();

        // Initialiseer de sliders op basis van de huidige waarden in de managers
        view.getVolumeSlider().setValue(view.getResourceManager().getMusicManager().getVolume() * 10);
        view.getSoundSlider().setValue(view.getResourceManager().getSfxManager().getVolume() * 10);

        addEventHandlers();
        updateView();
    }

    private void addEventHandlers() {
        // Volume Music Events
        view.getVolumeSlider().valueProperty().addListener((observable, oldValue, newValue) -> {
            setVolumeMusicSlider();
            setVolumeIcon();
        });

        // Volume Sound Events
        view.getSoundSlider().valueProperty().addListener((observable, oldValue, newValue) -> {
            setVolumeSoundSlider();
            setSoundIcon();
        });

        // Return Button
        view.getReturnButton().setOnAction(actionEvent -> {
            NavigationService.closeWindow(this.view);
        });
        GeneralEventhandlers.addHoverEffect(view.getReturnButton());
        GeneralEventhandlers.addSoundEffect(view.getReturnButton(), view.getResourceManager());

        // Theme Selector Events
        view.getThemeSelector().getRightButton().setOnAction(actionEvent -> {
            themePickerModel.increaseCurrentIndex();
            updateView();
            setResourceManagerTheme(Themes.values()[themePickerModel.getCurrentIndex()]);
        });

        view.getThemeSelector().getLeftButton().setOnAction(actionEvent -> {
            themePickerModel.decreaseCurrentIndex();
            updateView();
            setResourceManagerTheme(Themes.values()[themePickerModel.getCurrentIndex()]);
        });
    }

    private void setVolumeMusicSlider() {
        view.getResourceManager().getMusicManager().setVolume(view.getVolumeSlider().getValue());
    }

    private void setVolumeSoundSlider() {
        view.getResourceManager().getSfxManager().setVolume(view.getSoundSlider().getValue());
    }

    private void setVolumeIcon() {
        double currentValue = view.getVolumeSlider().getValue();
        String iconPath = (currentValue == 0) ? "mute_music.png" : "music.png";
        view.setVolumeIcon(new Image(getClass().getResource("/game/integration_project1_zaroc/ui/icons/" + iconPath).toExternalForm()));
    }

    private void setSoundIcon() {
        double currentValue = view.getSoundSlider().getValue();
        String iconPath = (currentValue == 0) ? "mute_sound.png" : "sound.png";
        view.setSoundIcon(new Image(getClass().getResource("/game/integration_project1_zaroc/ui/icons/" + iconPath).toExternalForm()));
    }

    private void updateView() {
        view.getThemeSelector().getLabel().setText(Themes.values()[themePickerModel.getCurrentIndex()].name());
    }

    public void setResourceManagerTheme(Themes theme) {
        view.getResourceManager().setTheme(theme);
    }

    @Override
    public void updateLayout(Object args) {
        view.layoutNodes();
    }
}