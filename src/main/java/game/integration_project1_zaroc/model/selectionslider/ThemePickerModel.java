package game.integration_project1_zaroc.model.selectionslider;

/**
 * Model for the theme selector, keeping track of the currently selected theme index.
 * The index wraps around in both directions across all available themes.
 */
public class ThemePickerModel {
    /** Total number of available themes.*/
    private final int AMOUNT_OF_THEMES = 4;
    /** Index of the currently selected theme. */
    private int currentIndex;

    /**
     * Creates a new ThemePickerModel with the first theme selected.
     */
    public ThemePickerModel() {
        this.currentIndex = 0;
    }

    /**
     * Goes to the next theme, wrapping back to the first after the last.
     */
    public void increaseCurrentIndex() {
        currentIndex = (currentIndex + 1) % AMOUNT_OF_THEMES;
    }

    /**
     * Goes back to the previous theme, wrapping to the last after the first.
     */
    public void decreaseCurrentIndex() {
        currentIndex = (currentIndex - 1 + AMOUNT_OF_THEMES) % AMOUNT_OF_THEMES;
    }


    /**
     * Returns the index of the currently selected theme.
     *
     * @return the current theme index (0-based)
     */
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * Sets the current theme index directly.
     *
     * @param index the index to set
     */
    public void setCurrentIndex(int index) {
        currentIndex = index;
    }


}
