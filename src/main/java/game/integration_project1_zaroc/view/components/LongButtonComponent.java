package game.integration_project1_zaroc.view.components;

import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;
import javafx.scene.paint.Color;

public class LongButtonComponent extends GeneralActionsComponent {

    public LongButtonComponent(ResourceManager resourceManager, Components path,String text){
        super(resourceManager,path);
        setText(text);
        setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
        setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
    }
}
