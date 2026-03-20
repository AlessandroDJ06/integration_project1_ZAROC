package game.integration_project1_zaroc.view.pages.loginview;

import game.integration_project1_zaroc.model.AppController;

public class LoginPresenter {
    private AppController model;
    private LoginView view;

    public LoginPresenter(AppController model,LoginView view){
        this.model = model;
        this.view = view;
        addEventHandlers();
    }

    private void addEventHandlers(){

    }
}
