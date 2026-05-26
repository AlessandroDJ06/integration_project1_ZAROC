package game.integration_project1_zaroc.view.components.slidercomponents;

import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public abstract class SliderComponent extends BorderPane {
    protected ResourceManager resourceManager;
    private Button leftButton;
    private Button rightButton;
    private Node centerNode;

    public SliderComponent(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        this.leftButton = new Button("<");
        this.rightButton = new Button(">");
        this.centerNode = getContent();
    }

    private void layoutNodes() {
        String textColor = resourceManager.getTheme().getTextColor();
        leftButton.setTextFill(Color.web(textColor));
        rightButton.setTextFill(Color.web(textColor));
        leftButton.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        rightButton.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));

        leftButton.setBackground(Background.EMPTY);
        rightButton.setBackground(Background.EMPTY);
        leftButton.setCursor(Cursor.HAND);
        rightButton.setCursor(Cursor.HAND);

        setLeft(leftButton);
        BorderPane.setAlignment(leftButton, Pos.CENTER_LEFT);
        setRight(rightButton);
        BorderPane.setAlignment(rightButton, Pos.CENTER_RIGHT);

        if (centerNode instanceof javafx.scene.layout.Region) {
            javafx.scene.layout.Region region = (javafx.scene.layout.Region) centerNode;
            region.setMaxWidth(90);
            region.setMinWidth(90);
        } else if (centerNode instanceof javafx.scene.image.ImageView) {
            ((javafx.scene.image.ImageView) centerNode).setFitWidth(60);
            ((ImageView) centerNode).setFitHeight(60);
            ((javafx.scene.image.ImageView) centerNode).setPreserveRatio(true);
            leftButton.setPadding(new Insets(0,25,0,0));
            rightButton.setPadding(new Insets(0,0,0,25));
        }

        setCenter(centerNode);
        BorderPane.setAlignment(centerNode, Pos.CENTER);
    }

    public void updateLayout(){
        this.getChildren().clear();
        layoutNodes();
    }

    protected abstract Node getContent();

    public Button getLeftButton() { return leftButton; }
    public Button getRightButton() { return rightButton; }
}