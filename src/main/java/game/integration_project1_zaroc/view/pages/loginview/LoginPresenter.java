package game.integration_project1_zaroc.view.pages.loginview;

import game.integration_project1_zaroc.dao.ZarocDaoException;
import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.model.players.HumanPlayer;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage; // Vergeet deze import niet

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginPresenter {
    private AppController model;
    private LoginView view;

    public LoginPresenter(AppController model, LoginView view){
        this.model = model;
        this.view = view;
        addEventHandlers();
    }

    private void addEventHandlers(){
        List<Button> buttons = Arrays.asList(
                view.getPlayAsGuest(),
                view.getLoginButton(),
                view.getReturnButton());
        for (Button button : buttons){
            GeneralEventhandlers.addHoverEffect(button);
        }

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
                if (model.isAllowedToUseDatabase()){
                    showGuestLoginDialog(true,"User not found or password wrong");
                } else {
                    showGuestLoginDialog(true,"database connection failed");
                }
                model.setAllowedToUseDatabase(false);

            }
        });

        view.getPlayAsGuest().setOnAction(event -> {
            showGuestLoginDialog(false, null);
        });

    }

    private void showGuestLoginDialog(boolean isError, String errorMsg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        if (isError) {
            alert.setTitle("Login Problem");
            alert.setHeaderText("ERROR: " + errorMsg);
        } else {
            alert.setTitle("Guest Login");
            alert.setHeaderText("Play as guest");
        }

        TextField nameInput = new TextField();
        nameInput.setPromptText("Name (min. 1 char)");

        VBox content = new VBox(10);
        String labelText = isError ? "continue as guest?" : "choose name:";
        content.getChildren().addAll(new Label(labelText), nameInput);
        alert.getDialogPane().setContent(content);

        ButtonType guestBtn = new ButtonType("Play as guest");
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(guestBtn, cancelBtn);

        Node guestButtonNode = alert.getDialogPane().lookupButton(guestBtn);
        guestButtonNode.setDisable(true);
        nameInput.textProperty().addListener((obs, old, newVal) ->
                guestButtonNode.setDisable(newVal.trim().isEmpty())
        );

        alert.showAndWait().ifPresent(type -> {
            if (type == guestBtn) {
                String name = nameInput.getText().trim();
                model.setPlayer1(new HumanPlayer(name, "gast@local.com"));
                model.setAllowedToUseDatabase(false);
                closeWindow();
            }
        });
    }

    private void closeWindow() {
        Stage stage = (Stage) view.getScene().getWindow();
        stage.close();
    }
}