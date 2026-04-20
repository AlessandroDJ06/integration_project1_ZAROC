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

    //deze zijn toegevoegd om de pause button beter te maken
    private double width;
    private double height;

    public ShortButtonComponent(ResourceManager resourceManager,String text){
        //standaard width en height waarden naar hier verplaatst ipv layoutNodes
        this(resourceManager, text, 150, 60);
    }

    public ShortButtonComponent(ResourceManager resourceManager, String text, double width, double height){
        super(text);
        this.resourceManager = resourceManager;
        this.width = width;
        this.height = height;
        layoutNodes();
    }

    public void layoutNodes(){
        ImageView imageView = new ImageView(resourceManager.getImage(Components.UNIVERSAL));
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(false);

        setGraphic(imageView);
        setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
        setFont(resourceManager.getFont(Fonts.PRESSSTART2PMEDIUM));
        setAlignment(Pos.CENTER);
        setTextAlignment(TextAlignment.CENTER);
        setBackground(Background.EMPTY);
        setContentDisplay(ContentDisplay.CENTER);
    }
}
