package game.integration_project1_zaroc.view.sharedlogic.utils;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class MusicManager {
private MediaPlayer backgroundMusic;
private double volume = 1;

public MusicManager(){
    URL resource = getClass().getResource("/game/integration_project1_zaroc/Audio/Music/backgroundMusic.mp3");
    if(resource != null){
        Media media = new Media(resource.toExternalForm());
        backgroundMusic = new MediaPlayer(media);
        backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
    }

}
public void startMusic(){
    if (backgroundMusic!=null) {
        backgroundMusic.setVolume(volume);
        backgroundMusic.play();
    }
}
    public void stopMusic(){
        if (backgroundMusic!=null) {
            backgroundMusic.stop();
        }
    }

    public void setVolume(double volume) {
        this.volume = volume/10;
        backgroundMusic.setVolume(this.volume);
    }

    public double getVolume() {
        return volume;
    }
}
