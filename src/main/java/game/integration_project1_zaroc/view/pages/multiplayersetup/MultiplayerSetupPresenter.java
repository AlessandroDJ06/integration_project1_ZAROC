package game.integration_project1_zaroc.view.pages.multiplayersetup;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.utils.Observer;
import game.integration_project1_zaroc.view.sharedlogic.NavigationService;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.Arrays;

public class MultiplayerSetupPresenter implements Observer {
    private MultiplayerSetupView view;
    private AppController model;

    public MultiplayerSetupPresenter(MultiplayerSetupView view, AppController model) {
        this.view = view;
        this.model = model;
        addEventHandlers();
    }

    private void addEventHandlers(){
        for (Button button : Arrays.asList(
                view.getReturnButton(),
                view.getHostGame(),
                view.getJoinGame())){
            GeneralEventhandlers.addHoverEffect(button);
            GeneralEventhandlers.addSoundEffect(button, view.getResourceManager());
        }

        view.getReturnButton().setOnAction(event -> {
            NavigationService.closeWindow(this.view);
        });

        view.getHostGame().setOnAction(event -> {
            model.setOnlineMultiplayer(true);
            model.setHost(true);
            NavigationService.closeWindow(this.view);
        });

        view.getJoinGame().setOnAction(event -> {
            model.setOnlineMultiplayer(true);
            model.setHost(false);
            NavigationService.closeWindow(this.view);
        });


    }

    @Override
    public void updateLayout(Object args) {
        view.layoutNodes();
    }

}
