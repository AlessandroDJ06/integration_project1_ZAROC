package game.integration_project1_zaroc.view.pages.settingsview;

import game.integration_project1_zaroc.view.components.buttons.TextButton;
import game.integration_project1_zaroc.view.components.slidercomponents.TextSliderComponent;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Themes;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class SettingsView extends BorderPane {

    private TextButton returnButton;
    private final ResourceManager resourceManager;
    private Slider volume;
    private Slider sound;
    private Text titel;
    private Text soundLabel;
    private Text volumeLabel;
    private Text themeLabel;
    private TextSliderComponent themeSelector;



    private ImageView volumeIconView;
    private ImageView soundIconView;


    private Image imgVolumeOn;
    private Image imgVolumeMuted;
    private Image imgSoundOn;
    private Image imgSoundMuted;

    public SettingsView(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    public void initialiseNodes() {
        returnButton = new TextButton(resourceManager, "X");
        volume = new Slider(0, 10, 10);
        sound = new Slider(0, 10, 10);
        titel = new Text("Instellingen");
        soundLabel = new Text("Geluid");
        volumeLabel = new Text("Muziek");
        themeLabel = new Text("Thema");


        volumeIconView = new ImageView();
        soundIconView = new ImageView();


        volumeIconView.setFitWidth(24);
        volumeIconView.setFitHeight(24);
        volumeIconView.setPreserveRatio(true);

        soundIconView.setFitWidth(24);
        soundIconView.setFitHeight(24);
        soundIconView.setPreserveRatio(true);

        imgVolumeOn = new Image(getClass().getResource("/game/integration_project1_zaroc/ui/icons/music.png").toExternalForm());
        imgVolumeMuted = new Image(getClass().getResource("/game/integration_project1_zaroc/ui/icons/mute_music.png").toExternalForm());
        imgSoundOn = new Image(getClass().getResource("/game/integration_project1_zaroc/ui/icons/sound.png").toExternalForm());
        imgSoundMuted = new Image(getClass().getResource("/game/integration_project1_zaroc/ui/icons/mute_sound.png").toExternalForm());

        volumeIconView.setImage(imgVolumeOn);
        soundIconView.setImage(imgSoundOn);
        this.themeSelector = new TextSliderComponent(resourceManager);
    }

    public void layoutNodes() {
        this.setPrefSize(600, 340);
        this.setMaxSize(600, 340);
        this.setMinSize(600, 340);

        Color textColor = Color.web(resourceManager.getTheme().getTextColor());


        titel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        titel.setFill(textColor);


        StackPane header = new StackPane();
        header.getChildren().addAll(titel, returnButton);
        StackPane.setAlignment(titel, Pos.CENTER);
        StackPane.setAlignment(returnButton, Pos.CENTER_RIGHT);
        header.setPadding(new Insets(30, 125, 0, 125));

        setTop(header);


        volumeLabel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        volumeLabel.setFill(textColor);
        soundLabel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        soundLabel.setFill(textColor);
        themeLabel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        themeLabel.setFill(textColor);

        volume.setMaxSize(250, 0);
        volume.setMinSize(250, 0);
        volume.setMajorTickUnit(1);
        volume.setMinorTickCount(0);
        volume.setSnapToTicks(true);

        sound.setMaxSize(250, 0);
        sound.setMinSize(250, 0);
        sound.setMajorTickUnit(1);
        sound.setMinorTickCount(0);
        sound.setSnapToTicks(true);


        HBox volumeControlBox = new HBox(15, volumeIconView, volume);
        volumeControlBox.setAlignment(Pos.CENTER);

        HBox soundControlBox = new HBox(15, soundIconView, sound);
        soundControlBox.setAlignment(Pos.CENTER);

        HBox themeControlBox = new HBox(30, themeSelector );
        themeControlBox.setAlignment(Pos.CENTER);

        VBox volumeBox = new VBox(10, volumeLabel, volumeControlBox);
        volumeBox.setAlignment(Pos.CENTER);

        VBox soundBox = new VBox(10, soundLabel, soundControlBox);
        soundBox.setAlignment(Pos.CENTER);

        VBox themeBox = new VBox(10, themeLabel, themeControlBox);
        themeBox.setAlignment(Pos.CENTER);


        VBox sliders = new VBox(30, volumeBox, soundBox, themeBox);
        sliders.setAlignment(Pos.TOP_CENTER);
        sliders.setPadding(new Insets(30, 0, 0, 0));

        setCenter(sliders);


        Image boardBackgroundImage = resourceManager.getImage(Components.PEGVIEW);
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(
                boardBackgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );
        setBackground(new Background(backgroundImage));
    }
    public void setVolumeIcon(Image image){
        volumeIconView.setImage(image);
    }
    public void setSoundIcon(Image image){
        soundIconView.setImage(image);
    }

    public TextButton getReturnButton() {
        return returnButton;
    }

    public Slider getVolumeSlider() {
        return volume;
    }

    public Slider getSoundSlider() {
        return sound;
    }

    public TextSliderComponent getThemeSelector(){return themeSelector;}

    public ResourceManager getResourceManager() {
        return resourceManager;
    }
}