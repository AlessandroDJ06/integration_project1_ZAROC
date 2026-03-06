package game.integration_project1_zaroc.view.components;

import game.integration_project1_zaroc.view.core.ResourceManager;
import game.integration_project1_zaroc.view.core.themes.Components;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;

public class GeneralActionsComponent extends Button {
    private ResourceManager resourceManager;
    private Components component;

    public GeneralActionsComponent(ResourceManager resourceManager,Components path){
        this.resourceManager = resourceManager;
        this.component = path;
        layoutNodes();
    }

    public void layoutNodes(){
        ImageView imageView = new ImageView(resourceManager.getImage(component));
        imageView.setFitHeight(88);
        imageView.setFitWidth(88);
        setGraphic(imageView);
        setBackground(Background.EMPTY);

    }
}
