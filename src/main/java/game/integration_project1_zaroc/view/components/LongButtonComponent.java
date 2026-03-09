package game.integration_project1_zaroc.view.components;

import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;

public class LongButtonComponent extends GeneralActionsComponent {

    public LongButtonComponent(ResourceManager resourceManager, Components path,String text){
        super(resourceManager,path);
        setText(text);
        setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
    }
}
