package game.integration_project1_zaroc.view.pages.createaccountview;

import game.integration_project1_zaroc.dao.ZarocDaoException;
import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.utils.Observer;
import game.integration_project1_zaroc.view.sharedlogic.NavigationService;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.profilePictures.ProfilePictures;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class CreateAccountPresenter implements Observer {
    private AppController model;
    private CreateAccountView view;
    private boolean isPlayerOne;

    public CreateAccountPresenter(AppController model, CreateAccountView view,boolean isPlayerOne) {
        this.model = model;
        this.view = view;
        this.isPlayerOne = isPlayerOne;
        view.getResourceManager().addObserver(this);
        addEventHandlers();
        updateView();
    }

    private void addEventHandlers() {
        List<Button> buttons = Arrays.asList(
                view.getCreateButton(),
                view.getReturnButton(),
                view.getProfilePicturePicker().getLeftButton(),
                view.getProfilePicturePicker().getRightButton()
        );

        for (Button button : buttons){
            GeneralEventhandlers.addHoverEffect(button);
            GeneralEventhandlers.addSoundEffect(button, view.getResourceManager());
        }



        view.getReturnButton().setOnAction(e -> NavigationService.closeWindow(this.view));

        view.getCreateButton().setOnAction(e -> {
            String username = view.getUsername();
            String email = view.getEmail();
            String password = view.getPassword();
            String profilePicture = ProfilePictures.values()[model.getProfilePicturePickerModel().getCurrentIndex()].getName();

            try {
                model.createAccount(username, email, password,profilePicture,isPlayerOne);
                NavigationService.closeWindow(this.view);
            } catch (ZarocDaoException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("Account creation failed");
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

    private void updateView(){
        view.getProfilePicturePicker().getImageView().setImage(view.getResourceManager().getProfilePicture(ProfilePictures.values()[model.getProfilePicturePickerModel().getCurrentIndex()]));
    }
    @Override
    public void updateLayout(Object args) {
        view.layoutNodes();
    }
}
