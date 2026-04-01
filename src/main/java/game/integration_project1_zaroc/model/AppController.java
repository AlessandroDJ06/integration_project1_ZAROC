package game.integration_project1_zaroc.model;

import game.integration_project1_zaroc.dao.*;
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
    private boolean allowedToUseDatabase;
    private PawnColorPickerModel colorOne;
    private PawnColorPickerModel colorTwo;
    private DifficultyPickerModel difficultyPicker;
    private StartingPlayerSelector startingPlayerSelector;
    private GamesDao gamesDao;
    private GameParticipationDao gameParticipationDao;
    private PlayersDao playersDao;

    private PawnColor player1Color;
    private PawnColor player2Color;

    private Player player1;
    private Player player2;

    private Game game;

    public AppController(boolean canConnect){
        this.allowedToUseDatabase = canConnect;
        this.difficultyPicker = new DifficultyPickerModel();

        player1 = null;

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

        this.gamesDao = new GamesDao();
        this.gameParticipationDao = new GameParticipationDao();
        this.playersDao = new PlayersDao();
    }

    public void createAccount(String username, String email, String password) throws ZarocDaoException {
        HumanPlayer player = new HumanPlayer(username, email);
        int id = playersDao.createHumanPlayer(player, password);
        player.setPlayerId(id);
        setPlayer1(player);
    }


    public boolean isLoggedIn() {
        return player1 != null;
    }

    public void setPlayer1Color() {
        this.player1Color = PawnColor.values()[colorOne.getCurrentIndex()];
        colorTwo.setCurrentIndexOtherPicker(player1Color.ordinal());
    }

    public void setPlayer2Color() {
        this.player2Color = PawnColor.values()[colorTwo.getCurrentIndex()];
        colorOne.setCurrentIndexOtherPicker(player2Color.ordinal());
    }

    public void createGame() {
        Difficulty gekozenDifficulty = Difficulty.values()[difficultyPicker.getCurrentIndex()];

        this.player2 = new AIPlayer(gekozenDifficulty, "jonas");

        if (allowedToUseDatabase){
            try {
                player2.setPlayerId(playersDao.createAiPlayer((AIPlayer) player2));
            } catch (ZarocDaoException e) {
                System.out.println("kon niet worden opgeslagen");
            }
        }

        this.player1Color = PawnColor.values()[colorOne.getCurrentIndex()];
        this.player2Color = PawnColor.values()[colorTwo.getCurrentIndex()];

        this.game = new Game(
                new GameParticipation(this.player1, this.player1Color),
                new GameParticipation(this.player2, this.player2Color)
        );

        this.game.getBoard().setupStart();
        this.game.setAllowedSave(allowedToUseDatabase);
        if (allowedToUseDatabase){
            createGameId();
            saveGameParticipations(this.game);
        }


        this.startingPlayerSelector.setPlayer1(player1);
        this.startingPlayerSelector.setPlayer2(player2);

        this.game.startNewTurn(startingPlayerSelector.getPlayers()[startingPlayerSelector.getCurrentIndex()]);
    }
    private void createGameId(){
        try {
            this.game.setGameId(gamesDao.createGame());
        } catch (ZarocDaoException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveGameParticipations(Game game){
        try {
            this.gameParticipationDao.createGameParticipation(game);
        } catch (ZarocDaoException e) {
            throw new RuntimeException(e);
        }

    }

    public void login(String username, String password) throws ZarocDaoException {
        HumanPlayer player = playersDao.getPlayerByUsername(username, password);

        if (player == null) {
            throw new ZarocDaoException("Gebruiker '" + username + "' niet gevonden");
        }

        setPlayer1(player);
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

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

}
