package game.integration_project1_zaroc.view.sharedlogic.utils;

import javafx.scene.media.AudioClip;

import java.net.URL;

public class SFXManager {
    private AudioClip pawnMove;
    private AudioClip buttonPress;
    private AudioClip undoSound;
    private AudioClip warningSound;
    private AudioClip winSound;
    private AudioClip lossSound;

    private double volume = 1;

    public SFXManager() {
        pawnMove = loadSound("/game/integration_project1_zaroc/Audio/SFX/pawn.mp3");
        buttonPress = loadSound("/game/integration_project1_zaroc/Audio/SFX/buttonPress.mp3");
        //undoSound = loadSound();
        //warningSound = loadSound();
    }

    private AudioClip loadSound(String path) {
        URL resource = getClass().getResource(path);
        return (resource != null) ? new AudioClip(resource.toExternalForm()) : null;
    }

    public void playPawnMove() {
        if (pawnMove != null) {
            pawnMove.setVolume(volume);
            pawnMove.play();

        }
    }

    public void playButtonPress() {
        if (buttonPress != null) {
            buttonPress.setVolume(volume);
            buttonPress.play();
        }
    }
    /**
     * Plays the undoSound AudioClip.
     * Only if undoSound != null.
     * */
    public void playUndoSound(){
        if (undoSound != null) {
            undoSound.setVolume(volume);
            undoSound.play();
        }
    }
    /**
     * Plays the warningSound AudioClip.
     * Only if warningSound != null.
     * */
    public void playWarningSound(){
        if (warningSound != null) {
            warningSound.setVolume(volume);
            warningSound.play();
        }
    }


    public void setVolume(double volume) {
        this.volume = volume/10;
    }

    public double getVolume() {
        return volume;
    }
}


