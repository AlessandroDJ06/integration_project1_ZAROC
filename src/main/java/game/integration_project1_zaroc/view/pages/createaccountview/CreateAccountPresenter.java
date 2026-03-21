package game.integration_project1_zaroc.view.pages.createaccountview;

import game.integration_project1_zaroc.dao.ZarocDaoException;
import game.integration_project1_zaroc.model.AppController;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class CreateAccountPresenter {
    private AppController model;
    private CreateAccountView view;

    public CreateAccountPresenter(AppController model, CreateAccountView view) {
        this.model = model;
        this.view = view;
        addEventHandlers();
    }

    private void addEventHandlers() {
        view.getReturnButton().setOnAction(e -> closeWindow());

        view.getCreateButton().setOnAction(e -> {
            String username = view.getUsername();
            String email = view.getEmail();
            String password = view.getPassword();

            try {
                model.createAccount(username, email, password);
                closeWindow();
            } catch (ZarocDaoException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Fout");
                alert.setHeaderText("Account aanmaken mislukt");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });
    }

    private void closeWindow() {
        Stage stage = (Stage) view.getScene().getWindow();
        stage.close();
    }
}
