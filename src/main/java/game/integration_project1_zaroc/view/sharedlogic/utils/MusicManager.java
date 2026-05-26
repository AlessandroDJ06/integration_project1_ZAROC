package game.integration_project1_zaroc.view.sharedlogic.utils;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
/**
 * Manages background music playback and volume control for the music.
 * */
public class MusicManager {
    /** MediaPlayer used for background music. */
private MediaPlayer backgroundMusic;

/** Current volume level, between 0.0-1.0.*/
private double volume = 1;

    /**
     * Creates a new MusicManager and loads the background music resource.
     * If the audio file is not found, the player is not initialized and playback methods will silently do nothing.
     * */
public MusicManager(){
    URL resource = getClass().getResource("/game/integration_project1_zaroc/Audio/Music/backgroundMusic.mp3");
    if(resource != null){
        Media media = new Media(resource.toExternalForm());
        backgroundMusic = new MediaPlayer(media);
        backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
    }

}
/**
 * Starts background music.
 * Only if backgroundMusic != null.
 * */
public void startMusic(){
    if (backgroundMusic!=null) {
        backgroundMusic.setVolume(volume);
        backgroundMusic.play();
    }
}

/**
 * Stops background music.
 * Only if backgroundMusic != null.
 * */
    public void stopMusic(){
        if (backgroundMusic!=null) {
            backgroundMusic.stop();
        }
    }

    /**
     * Sets the playback volume based on a slider scale of 1 to 10.
     * The value is divided by 10 to match the media players range of 0-1. */
    public void setVolume(double volume) {
        this.volume = volume/10;
        backgroundMusic.setVolume(this.volume);
    }
/**
 * Returns current volume on a scale of 0-1.
 * */
    public double getVolume() {
        return volume;
    }
}
