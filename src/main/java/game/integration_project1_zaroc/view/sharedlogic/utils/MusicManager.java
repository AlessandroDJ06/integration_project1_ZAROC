package game.integration_project1_zaroc.view.sharedlogic.utils;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

/**
 * Manages background music playback and volume control for the music.
 *
 */
public class MusicManager {
    /**
     * MediaPlayer used for background music.
     */
    private MediaPlayer backgroundMusic;
    private MediaPlayer gameMusic;

    /**
     * Current volume level, between 0.0-1.0.
     */
    private double volume = 1;

    /**
     * Creates a new MusicManager and loads the background music resource.
     * If the audio file is not found, the player is not initialized and playback methods will silently do nothing.
     *
     */
    public MusicManager() {
        URL background = getClass().getResource("/game/integration_project1_zaroc/Audio/Music/backgroundMusic.mp3");
        URL game = getClass().getResource("/game/integration_project1_zaroc/Audio/Music/game.mp3");
        if (game != null) {
            Media media = new Media(game.toExternalForm());
            gameMusic = new MediaPlayer(media);

        }
        if (background != null) {
            Media media = new Media(background.toExternalForm());
            backgroundMusic = new MediaPlayer(media);
            backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
        }

    }

    /**
     * Starts background music.
     * Only if backgroundMusic != null.
     *
     */
    public void startMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.setVolume(volume);
            backgroundMusic.play();
        }
    }

    /**
     * Stops background music.
     * Only if backgroundMusic != null.
     *
     */
    public void stopMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }

    /**
     * Sets the playback volume based on a slider scale of 1 to 10.
     * The value is divided by 10 to match the media players range of 0-1.
     */
    public void setVolume(double volume) {
        this.volume = volume / 10;
        if (backgroundMusic != null) backgroundMusic.setVolume(this.volume);
        if (gameMusic != null) gameMusic.setVolume(this.volume);
    }

    /**
     * Changes music from background to game and the other way around.
     * With fadein and fadeout
     *
     */
    public void changeMusic() {
        if (backgroundMusic == null || gameMusic == null) return;

        boolean backgroundIsPlaying = backgroundMusic.getStatus() == MediaPlayer.Status.PLAYING;
        MediaPlayer fadeOut = backgroundIsPlaying ? backgroundMusic : gameMusic;
        MediaPlayer fadeIn = backgroundIsPlaying ? gameMusic : backgroundMusic;

        Thread fadeThread = new Thread(() -> {
            fade(fadeOut, volume, 0);
            fadeOut.stop();
            fadeOut.setVolume(volume);

            fadeIn.setCycleCount(MediaPlayer.INDEFINITE);
            fadeIn.setVolume(0);
            fadeIn.play();
            fade(fadeIn, 0, volume);
        });

        fadeThread.setDaemon(true);
        fadeThread.start();
    }

    private void fade(MediaPlayer muziek, double van, double tot) {
        int stappen = 30;
        long intervalMs = 800 / stappen;
        for (int i = 1; i <= stappen; i++) {
            muziek.setVolume(van + (tot - van) * i / stappen);
            try {
                Thread.sleep(intervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        muziek.setVolume(tot);
    }


    /**
     * Returns current volume on a scale of 0-1.
     *
     */
    public double getVolume() {
        return volume;
    }
}
