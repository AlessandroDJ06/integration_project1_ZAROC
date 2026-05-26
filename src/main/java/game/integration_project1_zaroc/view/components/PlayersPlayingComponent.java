package game.integration_project1_zaroc.view.components;

import game.integration_project1_zaroc.view.components.buttons.TextButton;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.profilePictures.ProfilePictures;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.util.Arrays;
import java.util.List;

public class PlayersPlayingComponent extends HBox {
    private final ResourceManager resourceManager;
    private Label firstPlayer;
    private Label secondPlayer;
    private Circle firsPlayerPfpFrame;
    private Circle secondPlayerPfpFrame;
    private ProfilePictures playerOnePfp;
    private ProfilePictures playerTwoPfp;
    private ImageView colorPlayerOne;
    private ImageView colorPlayerTwo;
    private Button pauseButton;

    private Label afkTimer;



    public PlayersPlayingComponent(String firstPlayer,String secondPlayer,ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        initializeNodes(firstPlayer,secondPlayer);
        layoutNodes();
    }

    public PlayersPlayingComponent(ResourceManager resourceManager){
        this("NAME","NAME",resourceManager);
    }
    private void initializeNodes(String firstPlayer,String secondPlayer) {
        this.firstPlayer = new Label(firstPlayer);
        this.secondPlayer = new Label(secondPlayer);
        this.firsPlayerPfpFrame = new Circle(50,50,50);
        this.secondPlayerPfpFrame = new Circle(50,50,50);
        this.colorPlayerOne = new ImageView();
        this.colorPlayerTwo = new ImageView();
        this.playerOnePfp = ProfilePictures.EMPTY;
        this.playerTwoPfp = ProfilePictures.EMPTY;
        this.pauseButton = new TextButton(resourceManager,"II");
        this.afkTimer = new Label("");
    }

    private void layoutNodes(){
        Image image = resourceManager.getImage(Components.GAMEPLAYERS);

        BackgroundImage myBI = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false)
        );

        VBox nameSectionPlayer1 = new VBox(this.firstPlayer,this.colorPlayerOne);
        VBox nameSectionPlayer2 = new VBox(this.secondPlayer,this.colorPlayerTwo);

        nameSectionPlayer2.setAlignment(Pos.CENTER);
        nameSectionPlayer1.setAlignment(Pos.CENTER);

        setPlayerOnePfp(this.playerOnePfp);
        setPlayerTwoPfp(this.playerTwoPfp);

        firstPlayer.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        secondPlayer.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));
        afkTimer.setFont(resourceManager.getFont(Fonts.PRESSSTART2PLARGE));

        List<Label> setFontColors = Arrays.asList(firstPlayer,secondPlayer,afkTimer);

        for (Label label : setFontColors){
            label.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
        }

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);


        VBox middelsection = new VBox(pauseButton,afkTimer);
        afkTimer.setTranslateX(3);
        middelsection.setAlignment(Pos.CENTER);
        middelsection.setSpacing(20);

        getChildren().addAll(this.firsPlayerPfpFrame,nameSectionPlayer1,spacer1,middelsection,spacer2,nameSectionPlayer2,this.secondPlayerPfpFrame);

        setBackground(new Background(myBI));
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(15);
        setPrefSize(750,200);
        setMaxSize(750,200);
        setPadding(new Insets(0,15,0,15));
    }

    public void updateLayout(){
        this.getChildren().clear();
        layoutNodes();
    }

    public void setFirstPlayer(String name) {
        this.firstPlayer.setText(name);
    }

    public void setSecondPlayer(String name){
        this.secondPlayer.setText(name);
    }

    public void setPlayerOnePfp(ProfilePictures playerOnePfp) {
        this.firsPlayerPfpFrame.setFill(
                new ImagePattern(
                        resourceManager.getProfilePicture(playerOnePfp)
                )
        );
    }

    public void setPlayerTwoPfp(ProfilePictures playerTwoPfp) {
        this.secondPlayerPfpFrame.setFill(
                new ImagePattern(
                        resourceManager.getProfilePicture(playerTwoPfp)
                )
        );
    }

    public void setFirstPlayer(Label firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public void setColorPlayerTwo(Image colorPlayerTwo) {
        this.colorPlayerTwo.setImage(colorPlayerTwo);
    }

    public void setPointPlayerOne(Image colorPlayerOne) {
        this.colorPlayerOne.setImage(colorPlayerOne);
    }

    public Label getFirstPlayer() {
        return firstPlayer;
    }

    public Label getSecondPlayer() {
        return secondPlayer;
    }

    public Button getPauseButton() {
        return pauseButton;
    }

    public Label getAfkTimer() {
        return afkTimer;
    }
}
