package game.integration_project1_zaroc.view.components;

import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.pawncolors.PawnColorPaths;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;

public class ImageSliderComponent extends BorderPane {
    private ResourceManager resourceManager;
    private Button leftButton;
    private Button rightButton;
    private ImageView pawnColor;

    public ImageSliderComponent(ResourceManager resourceManager){
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes(){
        this.leftButton= new Button("<");
        this.rightButton= new Button(">");
        this.pawnColor = new ImageView();
    }

    private void layoutNodes(){
        leftButton.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        leftButton.setBackground(Background.EMPTY);

        rightButton.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        rightButton.setBackground(Background.EMPTY);

        setLeft(leftButton);
        BorderPane.setAlignment(leftButton, Pos.CENTER_LEFT);
        setRight(rightButton);
        BorderPane.setAlignment(rightButton,Pos.CENTER_RIGHT);
        setCenter(pawnColor);

        leftButton.setCursor(Cursor.HAND);
        rightButton.setCursor(Cursor.HAND);

    }

    public ImageView getPawnColor() {
        return pawnColor;
    }

    public Button getRightButton() {
        return rightButton;
    }

    public Button getLeftButton() {
        return leftButton;
    }
}
