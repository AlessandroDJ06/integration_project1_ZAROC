package game.integration_project1_zaroc.view.pages.loginview;

import game.integration_project1_zaroc.dao.ZarocDaoException;
import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.model.players.HumanPlayer;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage; // Vergeet deze import niet

public class LoginPresenter {
    private AppController model;
    private LoginView view;

    public LoginPresenter(AppController model, LoginView view){
        this.model = model;
        this.view = view;
        addEventHandlers();
    }

    private void addEventHandlers(){
        view.getReturnButton().setOnAction(event -> {
            closeWindow();
        });

        view.getLoginButton().setOnAction(event -> {
            String username = view.getUsername();
            String password = view.getPassword();

            try {
                model.login(username, password);
                closeWindow();
            } catch (ZarocDaoException e) {
                handleLoginFailure(username, e.getMessage());
            }
        });

    }

    private void handleLoginFailure(String attemptedName, String errorMsg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Login Probleem");
        alert.setHeaderText("Kon niet inloggen: " + errorMsg);
        alert.setContentText("Wilt u verdergaan als gastspeler? (Data wordt niet opgeslagen)");

        ButtonType guestBtn = new ButtonType("Speel als Gast");
        ButtonType cancelBtn = new ButtonType("Annuleren", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(guestBtn, cancelBtn);

        alert.showAndWait().ifPresent(type -> {
            if (type == guestBtn) {
                model.setPlayer1(new HumanPlayer(attemptedName, "gast@local.com"));
                model.getGame().setAllowedSave(false);
                closeWindow();
            }
        });
    }

    private void closeWindow() {
        Stage stage = (Stage) view.getScene().getWindow();
        stage.close();
    }
}