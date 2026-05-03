package game.integration_project1_zaroc.view.pages.playervsplayerview;

import game.integration_project1_zaroc.dao.UnfinishedGame;
import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.view.sharedlogic.NavigationService;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.profilePictures.ProfilePictures;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.util.Arrays;

public class PlayerVsPlayerPresenter {
    private PlayerVsPlayerView view;
    private AppController model;
    private UnfinishedGame playerTwo;

    public PlayerVsPlayerPresenter(PlayerVsPlayerView view, AppController model, UnfinishedGame playerTwo) {
        this.view = view;
        this.model = model;
        this.playerTwo = playerTwo;
        addEventHandlers();
        updateView();
    }

    private void addEventHandlers(){
        for (Button button : Arrays.asList(view.getReturnButton(), view.getCreateAccountPlayerTwo(), view.getLoginPlayerTwo(), view.getStartGame())){
            GeneralEventhandlers.addHoverEffect(button);
            GeneralEventhandlers.addSoundEffect(button, view.getResourceManager());
        }
        view.getReturnButton().setOnAction(e -> {
            model.setPlayer2(null);
            NavigationService.closeWindow(this.view);
        });

        view.getStartGame().setOnAction(event -> {
            NavigationService.closeWindow(this.view);
        });

        view.getLoginPlayerTwo().setOnAction(event -> {
            NavigationService.navigateToLoginView(view.getResourceManager(),this.model,false).showAndWait();

            if (model.isContinueInLocalPlayer()){
                if (model.getPlayer2() != null &&
                        model.getPlayer2().getUsername().equals(playerTwo.getCurrentUserName()) ||
                        model.getPlayer2().getUsername().equals(playerTwo.getOpponentName()) &&
                                !model.getPlayer2().getUsername().equals(model.getPlayer1().getUsername())){
                    updateView();
                } else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("ERROR");
                    alert.setHeaderText("wrong user");
                    alert.setContentText("The user logged in isn't the user who played the game!");
                    alert.showAndWait();
                    model.setPlayer2(null);
                    System.out.println("hier");
                }
            } else {
                if (model.getPlayer2() != null) {
                    updateView();
                }
            }
        });

        view.getCreateAccountPlayerTwo().setOnAction(event -> {
            NavigationService.navigateToCreateAccountView(view.getResourceManager(),model,false);
            if (model.getPlayer2() != null) {
                updateView();
            }
        });

    }

    private void updateView(){
        view.getPlayerOneName().setText(model.getPlayer1().getUsername());
        String pic1 = model.getPlayer1().getProfilePicture();
        if (pic1 != null) {
            view.getPlayerOnePfp().setImage(view.getResourceManager().getProfilePicture(ProfilePictures.valueOf(pic1)));
        }
        if (model.getPlayer2() != null){
            view.getPlayerTwoName().setText(model.getPlayer2().getUsername());
            view.getPlayerTwoPfp().setImage(view.getResourceManager().getProfilePicture(ProfilePictures.valueOf(model.getPlayer2().getProfilePicture())));

            view.getContent().getChildren().remove(view.getLoginButtons());
            view.getContent().getChildren().add(view.getPlayerTwoInfo());
        }
    }
}
