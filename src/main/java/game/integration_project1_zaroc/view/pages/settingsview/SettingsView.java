package game.integration_project1_zaroc.view.pages.settingsview;

import game.integration_project1_zaroc.view.components.buttons.TextButton;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class SettingsView extends BorderPane {

    private TextButton returnButton;
    private ResourceManager resourceManager;
    private Slider volume;
    private Slider sound;
    private Text titel;
    private Text soundLabel;
    private Text volumeLabel;



    public SettingsView(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    public void initialiseNodes(){
        returnButton = new TextButton(resourceManager,"X");
        volume = new Slider(0,10,10);
        sound = new Slider(0,10,10);
        titel = new Text("Settings");
        soundLabel = new Text("Sound");
        volumeLabel = new Text("Volume");


    }

    public void layoutNodes(){

        titel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        titel.setTranslateY(25);
        soundLabel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        volumeLabel.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        Region spacer = new Region();
        spacer.setMinWidth(returnButton.getMinWidth());
        // returnButton
        returnButton.setFont(resourceManager.getFont(Fonts.PRESSSTART2BUTTONSTYLE));
        HBox top = new HBox(spacer,titel, returnButton);
        setTop(top);
        setAlignment(top, Pos.TOP_CENTER);
        setMargin(top, new Insets(30,0,0,220));

        // sliders
        volume.setMaxSize(300,0);
        volume.setMinSize(300,0);
        sound.setMaxSize(300,0);
        sound.setMinSize(300,0);

        volume.setMajorTickUnit(1);
        volume.setMinorTickCount(0);
        volume.setSnapToTicks(true);

        sound.setMajorTickUnit(1);
        sound.setMinorTickCount(0);
        sound.setSnapToTicks(true);

        VBox volumeBox = new VBox(10, volumeLabel, volume);
        VBox soundBox = new VBox(10, soundLabel, sound);

        volumeBox.setAlignment(Pos.CENTER);
        soundBox.setAlignment(Pos.CENTER);

        VBox sliders = new VBox(60, volumeBox, soundBox);
        setCenter(sliders);
        sliders.setAlignment(Pos.TOP_CENTER);
        setMargin(sliders,new Insets(10,0,0,0));






        // background
        Image boardBackgroundImage = resourceManager.getImage(Components.PEGVIEW);

        BackgroundSize backgroundSize = new BackgroundSize(100,100,true,true,true,false);

        BackgroundImage backgroundImage = new BackgroundImage(
                boardBackgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );

        setBackground(new Background(backgroundImage));
        this.setMaxSize(600,340);
        this.setMinSize(600,340);
    }

    public TextButton getReturnButton() {
        return returnButton;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }
}
