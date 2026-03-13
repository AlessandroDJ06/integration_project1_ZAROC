package game.integration_project1_zaroc.view.components.buttons;

import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;

public class TextButton extends Button {
    private ResourceManager resourceManager;

    public TextButton(ResourceManager resourceManager, String text){
        super(text);
        setBackground(Background.EMPTY);
        setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
        setCursor(Cursor.HAND);
    }
}
