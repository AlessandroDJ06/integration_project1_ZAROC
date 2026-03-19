package game.integration_project1_zaroc.model;

import game.integration_project1_zaroc.model.gamelogic.Game;
import game.integration_project1_zaroc.model.gameinfo.GameParticipation;
import game.integration_project1_zaroc.model.gameinfo.PawnColor;
import game.integration_project1_zaroc.model.players.AIPlayer;
import game.integration_project1_zaroc.model.players.Difficulty;
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
        this.difficultyPicker = new DifficultyPickerModel();

        player1 = new HumanPlayer("Alessandro","test@gmail.com");
        player2 = new AIPlayer(Difficulty.values()[difficultyPicker.getCurrentIndex()],"jonas");

        this.colorOne = new PawnColorPickerModel(PawnColor.BLACK);
        this.colorTwo = new PawnColorPickerModel(PawnColor.WHITE);

        colorOne.setCurrentIndexOtherPicker(colorTwo.getCurrentIndexOtherPicker());
        colorTwo.setCurrentIndexOtherPicker(colorOne.getCurrentIndexOtherPicker());

        setPlayer1Color();
        setPlayer2Color();


        this.startingPlayerSelector = new StartingPlayerSelector();
        this.startingPlayerSelector.setPlayer1(player1);
        this.startingPlayerSelector.setPlayer2(player2);

        this.game = null;
    }

    public void setPlayer1Color() {
        this.player1Color = PawnColor.values()[colorOne.getCurrentIndex()];
        colorTwo.setCurrentIndexOtherPicker(player1Color.ordinal());
    }

    public void setPlayer2Color() {
        this.player2Color = PawnColor.values()[colorTwo.getCurrentIndex()];
        colorOne.setCurrentIndexOtherPicker(player2Color.ordinal());
    }

    public void createGame(){
        this.game = new Game(
                new GameParticipation(this.player1,this.player1Color),
                new GameParticipation(this.player2,this.player2Color)
        );
        this.game.getBoard().setupStart();
        this.game.startNewTurn(startingPlayerSelector.getPlayers()[startingPlayerSelector.getCurrentIndex()]);
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
