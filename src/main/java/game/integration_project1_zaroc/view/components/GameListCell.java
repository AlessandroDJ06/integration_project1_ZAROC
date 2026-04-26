package game.integration_project1_zaroc.view.components;

import game.integration_project1_zaroc.dao.UnfinishedGame;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.profilePictures.ProfilePictures;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.time.format.DateTimeFormatter;

public class GameListCell extends ListCell<UnfinishedGame> {
    private HBox content;

    private Label currentPlayerName;
    private ImageView currentPlayerPfp;

    private Label opponentPlayerName;
    private ImageView opponentPlayerPfp;

    private ResourceManager resourceManager;

    private Label dateAndTime;

    public GameListCell(ResourceManager resourceManager){
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes(){
        this.content = new HBox(15);
        this.currentPlayerName = new Label();
        this.currentPlayerPfp = new ImageView();
        this.opponentPlayerName = new Label();
        this.opponentPlayerPfp = new ImageView();
        this.dateAndTime = new Label();
    }

    private void layoutNodes(){
        this.currentPlayerPfp.setFitWidth(40);
        this.currentPlayerPfp.setFitHeight(40);

        this.opponentPlayerPfp.setFitHeight(40);
        this.opponentPlayerPfp.setFitWidth(40);

        this.currentPlayerName.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));
        this.opponentPlayerName.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));
        this.dateAndTime.setFont(resourceManager.getFont(Fonts.PRESSSTART2PSMALL));

        this.opponentPlayerName.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
        this.currentPlayerName.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
        this.dateAndTime.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        this.content.setAlignment(Pos.CENTER);

        this.content.getChildren().addAll(currentPlayerPfp,currentPlayerName,dateAndTime,opponentPlayerName,opponentPlayerPfp);
    }

    @Override
    protected void updateItem(UnfinishedGame game, boolean empty){
        super.updateItem(game,empty);

        if (empty || game == null){
            setGraphic(null);
            setStyle("-fx-background-color: transparent;");
        } else {
            this.currentPlayerName.setText(game.getCurrentUserName());
            this.currentPlayerPfp.setImage(
                    game.getCurrentPlayerPfp() == null
                            ? resourceManager.getProfilePicture(ProfilePictures.EMPTY)
                            : resourceManager.getProfilePicture(ProfilePictures.valueOf(game.getCurrentPlayerPfp()))
            );
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
            String formattedDate = game.getStartTime().format(formatter);
            this.dateAndTime.setText(formattedDate);
            this.opponentPlayerPfp.setImage(
                    game.getOpponentPfp() == null
                            ? resourceManager.getProfilePicture(ProfilePictures.EMPTY)
                            : resourceManager.getProfilePicture(ProfilePictures.valueOf(game.getOpponentPfp()))
            );
            this.opponentPlayerName.setText(game.getOpponentName());

            if (getIndex() % 2 == 0) {
                setStyle("-fx-background-color:" + this.resourceManager.getTheme().getColor()+ ";");
            } else {
                setStyle("-fx-background-color: transparent;");
            }
            setGraphic(content);
        }
    }
}
