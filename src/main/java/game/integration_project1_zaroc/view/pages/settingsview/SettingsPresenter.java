package game.integration_project1_zaroc.view.pages.settingsview;


import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.model.selectionslider.ThemePickerModel;
import game.integration_project1_zaroc.utils.Observer;
import game.integration_project1_zaroc.view.sharedlogic.NavigationService;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Themes;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.scene.image.Image;

/**
 * Presenter for the settings screen.
 * Handles volume control, sound effects, and theme selection,
 * and keeps the view in sync with the model.
 */
public class SettingsPresenter implements Observer {

    /** The settings view this presenter manages.*/
    private SettingsView view;
    /** The connection to the model.*/
    private AppController model;
    /** Model responsible for keeping track of the selected theme index.*/
    private ThemePickerModel themePickerModel;


    /**
     * Creates a new SettingsPresenter.
     * It initializes sliders to the correct values from the SFXManager and MusicManager.
     * registers event handlers.
     *
     * @param view the setting view to manage.
     * @param model the connection to the model.
     * */
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

    /**Helper method for registering all event handelers for the settings view.
     * Covers the volume slider, sound slider, return button and theme selector buttons.
     * */
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

    /**Helper method for setting the volume slider to the correct value found in the MusicManager.*/
    private void setVolumeMusicSlider() {
        view.getResourceManager().getMusicManager().setVolume(view.getVolumeSlider().getValue());
    }
    /**Helper method for setting the sound slider to the correct value found in the SFXManager.*/
    private void setVolumeSoundSlider() {
        view.getResourceManager().getSfxManager().setVolume(view.getSoundSlider().getValue());
    }

    /** Helper method for setting the music volume icon.
     * Displays a mute icon when value is zero, otherwise the normal icon.*/
    private void setVolumeIcon() {
        double currentValue = view.getVolumeSlider().getValue();
        String iconPath = (currentValue == 0) ? "mute_music.png" : "music.png";
        view.setVolumeIcon(new Image(getClass().getResource("/game/integration_project1_zaroc/ui/icons/" + iconPath).toExternalForm()));
    }

    /** Helper method for setting the sound volume icon.
     * Displays a mute icon when value is zero, otherwise the normal icon.*/
    private void setSoundIcon() {
        double currentValue = view.getSoundSlider().getValue();
        String iconPath = (currentValue == 0) ? "mute_sound.png" : "sound.png";
        view.setSoundIcon(new Image(getClass().getResource("/game/integration_project1_zaroc/ui/icons/" + iconPath).toExternalForm()));
    }

    /** Updates the theme selector label to reflect the current selected theme.*/
    private void updateView() {
        view.getThemeSelector().getLabel().setText(Themes.values()[themePickerModel.getCurrentIndex()].name());
    }

    /**
     * Sets the given theme to the resource manager, updating the looks of the game.
     *
     * @param theme the theme to apply.
     */
    public void setResourceManagerTheme(Themes theme) {
        view.getResourceManager().setTheme(theme);
    }

    /**
     * Called when the resource manager notifies observers of a layout change.
     * Triggers a full layout refresh on the view.
     */
    @Override
    public void updateLayout(Object args) {
        view.layoutNodes();
    }
}