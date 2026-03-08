package game.integration_project1_zaroc.view.gamesetupview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.view.utils.GeneralEventhandlers;
import javafx.scene.control.Button;

import java.util.Arrays;
import java.util.List;

public class GameSetupPresenter {
    private AppController model;
    private GameSetupView view;
    private List<Button> buttons;
    private GeneralEventhandlers generalEventhandlers;

    public GameSetupPresenter(GameSetupView view ,AppController appController){
        this.view = view;
        this.model = appController;
        this.buttons = Arrays.asList(view.getProfileButton(),view.getSettingsButton(),view.getInfoButton());
        this.generalEventhandlers = new GeneralEventhandlers();
        addEventHandlers();
        updateView();
    }

    private void addEventHandlers(){
        for (Button button : buttons){
            generalEventhandlers.addHoverEffect(button);
        }
    }

    private void updateView(){

    }
}
