package game.integration_project1_zaroc.view.sharedlogic.resource_manager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.pawncolors.PawnColorPaths;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.pawncolors.PawnSideViews;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.profilePictures.ProfilePictures;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Themes;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ResourceManager {
    private  Map<String, Image> loadedImages;
    private  Map<String, Image> loadedProfileImages;
    private  Map<Fonts, Font> loadedFonts;
    private  Map<PawnColorPaths,Image> loadedPawnColors;
    private  Map<PawnSideViews,Image> loadedSideViews;
    private Themes theme;

    public ResourceManager(Themes theme){
        this.loadedImages = new HashMap<>();
        this.loadedProfileImages = new HashMap<>();
        this.loadedFonts = new HashMap<>();
        this.loadedPawnColors = new HashMap<>();
        this.loadedSideViews = new HashMap<>();
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
            loadedProfileImages.put(key,
                    new Image(
                            Objects.requireNonNull(getClass().getResourceAsStream(profilePicture.getPath()))
                    )
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

    public Image getPawnColor(PawnColorPaths color){
        if(!loadedPawnColors.containsKey(color)){
            loadedPawnColors.put(color,
                    new Image(
                            Objects.requireNonNull(getClass().getResourceAsStream(color.getPath()))
                    )
            );
        }
        return loadedPawnColors.get(color);
    }

    public ImageView getPawnImageView(PawnColorPaths color){
        ImageView pawnImageview = new ImageView(this.getPawnColor(color));
        pawnImageview.setScaleX(1.3);
        pawnImageview.setScaleY(1.3);
        return pawnImageview;
    }

    public Image getPawnSideView(PawnSideViews sideView){
        if (!loadedSideViews.containsKey(sideView)){
            loadedSideViews.put(sideView,
                    new Image(
                            Objects.requireNonNull(getClass().getResourceAsStream(sideView.getPath()))
                    )
            );
        }
        return loadedSideViews.get(sideView);
    }

    public Themes getTheme() {
        return theme;
    }
    public void setTheme(Themes theme){this.theme = theme;}
}
