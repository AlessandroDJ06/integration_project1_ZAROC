package game.integration_project1_zaroc.model;

import game.integration_project1_zaroc.model.gameinfo.Game;
import game.integration_project1_zaroc.model.gameinfo.GameParticipation;
import game.integration_project1_zaroc.model.gameinfo.PawnColor;
import game.integration_project1_zaroc.model.players.HumanPlayer;
import game.integration_project1_zaroc.model.players.Player;
import game.integration_project1_zaroc.model.selectionslider.DifficultyPickerModel;
import game.integration_project1_zaroc.model.selectionslider.PawnColorPickerModel;
import game.integration_project1_zaroc.model.selectionslider.StartingPlayerSelector;

public class AppController {
    private PawnColorPickerModel colorOne;
    private PawnColorPickerModel colorTwo;
    private DifficultyPickerModel difficultyPicker;
    private StartingPlayerSelector startingPlayerSelector;

    private PawnColor player1Color;
    private PawnColor player2Color;

    private Player player1;
    private Player player2;

    private Game game;

    public AppController(){
        player1 = new HumanPlayer("Alessandro","test@gmail.com");
        player2 = new HumanPlayer("jonas","test@gmail.com");

        this.colorOne = new PawnColorPickerModel(PawnColor.BLACK);
        this.colorTwo = new PawnColorPickerModel(PawnColor.WHITE);

        colorOne.setCurrentIndexOtherPicker(colorTwo.getCurrentIndexOtherPicker());
        colorTwo.setCurrentIndexOtherPicker(colorOne.getCurrentIndexOtherPicker());

        this.difficultyPicker = new DifficultyPickerModel();
        this.startingPlayerSelector = new StartingPlayerSelector();

        this.game = null;

    }

    public void setPlayer1Color() {
        this.player1Color = PawnColor.values()[colorOne.getCurrentIndex()];
    }

    public void setPlayer2Color() {
        this.player2Color = PawnColor.values()[colorTwo.getCurrentIndex()];
    }

    public void createGame(){
        this.game = new Game(
                new GameParticipation(this.player1,this.player1Color),
                new GameParticipation(this.player2,this.player2Color)
        );

        System.out.println(game.getPlayer1().getUsername());
        System.out.println(game.getPlayer2().getUsername());
    }


    public Game getGame() {
        return game;
    }

    public PawnColorPickerModel getColorOne() {
        return colorOne;
    }

    public StartingPlayerSelector getStartingPlayerSelector() {
        return startingPlayerSelector;
    }

    public DifficultyPickerModel getDifficultyPicker() {
        return difficultyPicker;
    }

    public PawnColorPickerModel getColorTwo() {
        return colorTwo;
    }
}
