package game.integration_project1_zaroc.view.pages.startview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.scene.control.Button;

import java.util.Arrays;
import java.util.List;

public class StartPresenter {
    private AppController model;
    private StartView view;
    private List<Button> buttons;

    public StartPresenter(AppController model , StartView view){
        this.model = model;
        this.view = view;
        this.buttons = Arrays.asList(
                view.getInfoButton(),
                view.getSettingsButton(),
                view.getLeaderboardButton(),
                view.getCreateAccountButton(),
                view.getLoginButton()
        );
        addEventHandlers();
    }

    private void addEventHandlers(){
        for (Button button : buttons){
            GeneralEventhandlers.addHoverEffect(button);
        }


    }
}
