package game.integration_project1_zaroc.view.pages.ruleview;

import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;

import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class TestView extends BorderPane {
    private RuleView ruleView;
    private ResourceManager resourceManager;

    public TestView(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }
    public void initialiseNodes(){
        ruleView=new RuleView(resourceManager);
    }
    public void layoutNodes(){
        setCenter(ruleView);
        this.setStyle("-fx-background-color: " + this.resourceManager.getTheme().getColor() + ";");
        Image boardBackgroundImage = resourceManager.getImage(Components.PEGVIEW);
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(
                boardBackgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );

        this.setBackground(new Background(backgroundImage));
    }


}
