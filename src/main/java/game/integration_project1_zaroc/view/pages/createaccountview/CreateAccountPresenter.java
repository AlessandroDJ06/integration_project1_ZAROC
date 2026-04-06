package game.integration_project1_zaroc.view.pages.createaccountview;

import game.integration_project1_zaroc.dao.ZarocDaoException;
import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.profilePictures.ProfilePictures;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class CreateAccountPresenter {
    private AppController model;
    private CreateAccountView view;

    public CreateAccountPresenter(AppController model, CreateAccountView view) {
        this.model = model;
        this.view = view;
        addEventHandlers();
        updateView();
    }

    private void addEventHandlers() {
        view.getReturnButton().setOnAction(e -> closeWindow());

        view.getCreateButton().setOnAction(e -> {
            String username = view.getUsername();
            String email = view.getEmail();
            String password = view.getPassword();
            String profilePicture = ProfilePictures.values()[model.getProfilePicturePickerModel().getCurrentIndex()].getName();

            try {
                model.createAccount(username, email, password,profilePicture);
                closeWindow();
            } catch (ZarocDaoException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Fout");
                alert.setHeaderText("Account aanmaken mislukt");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        view.getProfilePicturePicker().getLeftButton().setOnAction(event -> {
            model.getProfilePicturePickerModel().decreaseCurrentIndex();
            updateView();
        });

        view.getProfilePicturePicker().getRightButton().setOnAction(event -> {
            model.getProfilePicturePickerModel().increaseCurrentIndex();
            updateView();
        });
    }

    private void closeWindow() {
        Stage stage = (Stage) view.getScene().getWindow();
        stage.close();
    }

    private void updateView(){
        view.getProfilePicturePicker().getImageView().setImage(view.getResourceManager().getProfilePicture(ProfilePictures.values()[model.getProfilePicturePickerModel().getCurrentIndex()]));
    }
}
