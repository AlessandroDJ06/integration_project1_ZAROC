package game.integration_project1_zaroc.view.pages.startview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.utils.Observer;
import game.integration_project1_zaroc.view.sharedlogic.NavigationService;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.scene.control.Button;
import java.util.Arrays;
import java.util.List;


public class StartPresenter implements Observer {
    private AppController model;
    private StartView view;
    private List<Button> buttons;

    public StartPresenter(AppController model , StartView view){
        this.model = model;
        this.view = view;
        view.getResourceManager().addObserver(this);
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
            GeneralEventhandlers.addSoundEffect(button, view.getResourceManager());
        }

        view.getLoginButton().setOnAction(event -> {
            NavigationService.navigateToLoginView(view.getResourceManager(),this.model,true).showAndWait();
            if (model.isLoggedIn()) {
                NavigationService.navigateToGameModeSelection(view.getResourceManager(),this.model,this.view);
            }
        });

        view.getCreateAccountButton().setOnAction(event -> {
            NavigationService.navigateToCreateAccountView(view.getResourceManager(),this.model,true).showAndWait();
            if (model.isLoggedIn()) {
                NavigationService.navigateToGameModeSelection(view.getResourceManager(),this.model,this.view);
            }
        });

        view.getLeaderboardButton().setOnAction(actionEvent -> {
            NavigationService.navigateToLeaderboard(view.getResourceManager(),this.model).showAndWait();
        });

        view.getSettingsButton().setOnAction(actionEvent -> {
            NavigationService.navigateToSettings(view.getResourceManager(),this.model).showAndWait();
        });
    }

    @Override
    public void updateLayout(Object args) {
        view.layoutNodes();
    }
}
