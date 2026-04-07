package game.integration_project1_zaroc.view.components.slidercomponents;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

public class ImageSliderComponent extends SliderComponent {
    private ImageView imageView;

    public ImageSliderComponent(ResourceManager resourceManager) {
        super(resourceManager);
    }

    @Override
    protected Node getContent() {
        if (imageView == null) {
            imageView = new ImageView();
        }
        return imageView;
    }

    public ImageView getImageView() {
        return imageView;
    }
}