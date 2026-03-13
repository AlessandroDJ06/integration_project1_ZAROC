package game.integration_project1_zaroc.view.components.slidercomponents;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

public class ImageSliderComponent extends SliderComponent {
    private ImageView pawnColor;

    public ImageSliderComponent(ResourceManager resourceManager) {
        super(resourceManager);
    }

    @Override
    protected Node getContent() {
        if (pawnColor == null) {
            pawnColor = new ImageView();
        }
        return pawnColor;
    }

    public ImageView getPawnColor() {
        return pawnColor;
    }
}