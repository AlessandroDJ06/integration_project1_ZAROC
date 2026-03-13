package game.integration_project1_zaroc.view.components.slidercomponents;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class TextSliderComponent extends SliderComponent {
    private Label label;

    public TextSliderComponent(ResourceManager resourceManager) {
        super(resourceManager);
    }

    @Override
    protected Node getContent() {
        if (label == null) {
            label = new Label();
            label.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
            label.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSLIDER));
            label.setAlignment(Pos.CENTER);
        }
        return label;
    }

    public Label getLabel() {
        return label;
    }
}