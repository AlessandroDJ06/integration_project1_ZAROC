package game.integration_project1_zaroc.view.sharedlogic.utils;

import javafx.scene.media.AudioClip;

import java.net.URL;
/**
 * Manages sound effects playback and volume control for the SFX.
 * */
public class SFXManager {
    /**AudioClip used for audioclip pawnMove. */
    private AudioClip pawnMove;
    /** AudioClip used for audioclip buttonPress.*/
    private AudioClip buttonPress;
    /** Current volume level, between 0.0-1.0.*/
    private double volume = 1;


    /**
     * Creates a new SFXManager and loads the Audioclips using the helpermethode.
     * */
    public SFXManager() {
        pawnMove = loadSound("/game/integration_project1_zaroc/Audio/SFX/pawn.mp3");
        buttonPress = loadSound("/game/integration_project1_zaroc/Audio/SFX/buttonPress.mp3");
    }

    /** Loads the audioclips.
     * If the audio file is not found, the AudioClip is not initialized and playback methods will silently do nothing.
     * */
    private AudioClip loadSound(String path) {
        URL resource = getClass().getResource(path);
        return (resource != null) ? new AudioClip(resource.toExternalForm()) : null;
    }

    /**
     * Plays the PawnMove AudioClip.
     * Only if PawnMove != null.
     * */
    public void playPawnMove() {
        if (pawnMove != null) {
            pawnMove.setVolume(volume);
            pawnMove.play();

        }
    }
    /**
     * Plays the ButtonPress AudioClip.
     * Only if ButtonPress != null.
     * */
    public void playButtonPress() {
        if (buttonPress != null) {
            buttonPress.setVolume(volume);
            buttonPress.play();
        }
    }
    /**
     * Sets the playback volume based on a slider scale of 1 to 10.
     * The value is divided by 10 to match the media players range of 0-1. */
    public void setVolume(double volume) {
        this.volume = volume/10;
    }
    /**
     * Returns current volume on a scale of 0-1.
     * */
    public double getVolume() {
        return volume;
    }
}


