package game.integration_project1_zaroc.view.components.buttons;

import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class ShortButtonComponent extends Button {
    private ResourceManager resourceManager;

    public ShortButtonComponent(ResourceManager resourceManager,String text){
        super(text);
        this.resourceManager = resourceManager;
        layoutNodes();
    }

    public void layoutNodes(){
        ImageView imageView = new ImageView(resourceManager.getImage(Components.UNIVERSAL));
        imageView.setFitWidth(150);
        imageView.setFitHeight(60);
        imageView.setPreserveRatio(false);

        setGraphic(imageView);
        setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
        setFont(resourceManager.getFont(Fonts.PRESSSTART2BUTTONSTYLE));
        setAlignment(Pos.CENTER);
        setTextAlignment(TextAlignment.CENTER);
        setBackground(Background.EMPTY);
        setContentDisplay(ContentDisplay.CENTER);
    }
}
