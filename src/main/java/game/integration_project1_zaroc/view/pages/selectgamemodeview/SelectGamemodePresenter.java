package game.integration_project1_zaroc.view.pages.selectgamemodeview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.scene.control.Button;

import java.util.Arrays;
import java.util.List;


public class SelectGamemodePresenter {
    private SelectGamemodeView view;
    private AppController model;
    private List<Button> buttons;

    public SelectGamemodePresenter(AppController model , SelectGamemodeView view){
        this.view = view;
        this.model = model;
        this.buttons = Arrays.asList(
                view.getInfoButton(),
                view.getSettingsButton(),
                view.getProfileButton(),
                view.getPlayerVsAiButton(),
                view.getPlayerVsPlayerButton(),
                view.getUnfinishedGamesButton());
        addEventHandlers();
    }

    private void addEventHandlers(){
        for (Button button : buttons){
            GeneralEventhandlers.addHoverEffect(button);
        }

    }
}
