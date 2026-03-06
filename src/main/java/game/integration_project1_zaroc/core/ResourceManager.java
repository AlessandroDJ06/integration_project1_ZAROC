package game.integration_project1_zaroc.core;
import game.integration_project1_zaroc.core.fonts.Fonts;
import game.integration_project1_zaroc.core.profilePictures.ProfilePictures;
import game.integration_project1_zaroc.core.themes.Components;
import game.integration_project1_zaroc.core.themes.Themes;
import javafx.scene.image.Image;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ResourceManager {
    private  Map<String, Image> loadedImages;
    private  Map<String, Image> loadedProfileImages;
    private  Map<Fonts, Font> loadedFonts;
    private Themes theme;

    public ResourceManager(Themes theme){
        this.loadedImages = new HashMap<>();
        this.loadedProfileImages = new HashMap<>();
        this.loadedFonts = new HashMap<>();
        this.theme=theme;
    }

    public Image getImage(Components componentType) {
        String fullPath = componentType.getPath(this.theme);

        if (!loadedImages.containsKey(fullPath)) {
            loadedImages.put(fullPath, new Image(
                    Objects.requireNonNull(getClass().getResourceAsStream(fullPath)))
            );
        }
        return loadedImages.get(fullPath);
    }

    public Image getProfilePicture(ProfilePictures profilePicture){
        String key = profilePicture.getName();

        if (!loadedProfileImages.containsKey(key)){
            loadedProfileImages.put(key, new Image(
                    Objects.requireNonNull(getClass().getResourceAsStream(profilePicture.getPath())))
            );
        }
        return loadedProfileImages.get(key);
    }

    public Font getFont(Fonts font){
        if (!loadedFonts.containsKey(font)){
            loadedFonts.put(font, Font.loadFont(getClass().getResourceAsStream(font.getPath()), font.getWeight()));
        }

        return loadedFonts.get(font);
    }

    public Themes getTheme() {
        return theme;
    }
}
