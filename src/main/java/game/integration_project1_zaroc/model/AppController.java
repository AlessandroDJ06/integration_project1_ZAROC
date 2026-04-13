package game.integration_project1_zaroc.model;

import game.integration_project1_zaroc.dao.*;
import game.integration_project1_zaroc.model.boardinfo.Peg;
import game.integration_project1_zaroc.model.gamelogic.Game;
import game.integration_project1_zaroc.model.gameinfo.GameParticipation;
import game.integration_project1_zaroc.model.gameinfo.PawnColor;
import game.integration_project1_zaroc.model.gamelogic.Move;
import game.integration_project1_zaroc.model.gamelogic.MoveNumber;
import game.integration_project1_zaroc.model.gamelogic.Turn;
import game.integration_project1_zaroc.model.players.AIPlayer;
import game.integration_project1_zaroc.model.players.HumanPlayer;
import game.integration_project1_zaroc.model.players.Player;
import game.integration_project1_zaroc.model.selectionslider.PawnColorPickerModel;
import game.integration_project1_zaroc.model.selectionslider.ProfilePicturePickerModel;
import game.integration_project1_zaroc.model.selectionslider.StartingPlayerSelector;

import java.util.ArrayList;
import java.util.List;


public class AppController {
    private boolean allowedToUseDatabase;
    private PawnColorPickerModel colorOne;
    private PawnColorPickerModel colorTwo;
    private StartingPlayerSelector startingPlayerSelector;
    private ProfilePicturePickerModel profilePicturePickerModel;
    private GamesDao gamesDao;
    private GameParticipationDao gameParticipationDao;
    private PlayersDao playersDao;
    private UnfinishedGamesDao unfinishedGamesDao;

    private PawnColor player1Color;
    private PawnColor player2Color;

    private Player player1;
    private Player player2;

    private Game game;

    public AppController(boolean canConnect){
        this.allowedToUseDatabase = canConnect;

        player1 = null;

        this.colorOne = new PawnColorPickerModel(PawnColor.BLACK);
        this.colorTwo = new PawnColorPickerModel(PawnColor.WHITE);

        colorOne.setCurrentIndexOtherPicker(colorTwo.getCurrentIndexOtherPicker());
        colorTwo.setCurrentIndexOtherPicker(colorOne.getCurrentIndexOtherPicker());

        setPlayer1Color();
        setPlayer2Color();


        this.startingPlayerSelector = new StartingPlayerSelector();
        this.profilePicturePickerModel = new ProfilePicturePickerModel();
        this.startingPlayerSelector.setPlayer1(player1);
        this.startingPlayerSelector.setPlayer2(player2);

        this.game = null;

        this.gamesDao = new GamesDao();
        this.gameParticipationDao = new GameParticipationDao();
        this.playersDao = new PlayersDao();
        this.unfinishedGamesDao = new UnfinishedGamesDao();
    }


    public void createAccount(String username, String email, String password, String profilePicture, boolean isPlayerOne) throws ZarocDaoException {
        HumanPlayer player = new HumanPlayer(username, email);
        player.setProfilePicture(profilePicture);
        int id = playersDao.createHumanPlayer(player, password);
        player.setPlayerId(id);

        // Check voor welke speler dit is
        if (isPlayerOne) {
            setPlayer1(player);
        } else {
            setPlayer2(player);
        }
    }

    public void login(String username, String password, boolean isPlayerOne) throws ZarocDaoException {
        HumanPlayer player = playersDao.getPlayerByUsername(username, password);

        if (player == null) {
            throw new ZarocDaoException("Gebruiker '" + username + "' niet gevonden");
        }

        if (isPlayerOne) {
            setPlayer1(player);
        } else {
            setPlayer2(player);
        }
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

    public void resumeGame(int gameId){

        try{
            List<MovesUnfinishedGame> movesUnfinishedGame = unfinishedGamesDao.fetchMovesUnfinishedGame(gameId);
            ArrayList<Turn> turns = new ArrayList<>();

            this.game = new Game(
                    new GameParticipation(player1,this.player1Color),
                    new GameParticipation(player2,this.player2Color)
            );
            this.game.getBoard().setupStart();

            this.game.setGameId(gameId);

            for (MovesUnfinishedGame turnUnfinishedGame : movesUnfinishedGame){
                Player currentPlayer = (turnUnfinishedGame.getUsername().equals(player1.getUsername()) ? player1:player2);
                if (turns.isEmpty()){
                    Turn turn = new Turn(currentPlayer);
                    turn.setTurnNumber(0);
                    turn.setTurnId(turnUnfinishedGame.getTurnId());
                    turns.add(turn);
                } else if (turns.getLast().getTurnId() != turnUnfinishedGame.getTurnId()) {
                    Turn turn = new Turn(currentPlayer);
                    turn.setTurnId(turnUnfinishedGame.getTurnId());
                    turn.setTurnNumber(turns.size());
                    turns.add(turn);
                }

                if (turns.getLast().getTurnId() == turnUnfinishedGame.getTurnId()){
                    MoveNumber moveNumber = turnUnfinishedGame.getMoveNumber();
                    Peg startingPeg = this.game.getBoard().getPegPosition(turnUnfinishedGame.getStartY(), turnUnfinishedGame.getStartX());
                    Peg endPeg = this.game.getBoard().getPegPosition(turnUnfinishedGame.getEndY(), turnUnfinishedGame.getEndX());
                    Move move = new Move(moveNumber,startingPeg,endPeg);
                    move.setStartTime(turnUnfinishedGame.getStartTime());
                    move.setEndTime(turnUnfinishedGame.getEndTime());

                    game.executeMoveUnfinishedGame(move);

                    if (move.getMoveNumber() == MoveNumber.FIRST_MOVE){
                        turns.getLast().setMoveOne(move);
                    } else {
                        turns.getLast().setMoveTwo(move);
                    }
                }
            }

            this.game.setTurns(turns);

            if (!turns.isEmpty()) {
                Turn lastTurn = turns.getLast();

                if (lastTurn.getSecondMove() != null) {
                    this.game.switchCurrentPlayer();
                }
            }

        } catch (ZarocDaoException e) {
            throw new RuntimeException(e);
        }

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

    public PawnColorPickerModel getColorTwo() {
        return colorTwo;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Player getPlayer1() {
        return player1;
    }

    public ProfilePicturePickerModel getProfilePicturePickerModel() {
        return profilePicturePickerModel;
    }

    public void setAllowedToUseDatabase(boolean allowedToUseDatabase) {
        this.allowedToUseDatabase = allowedToUseDatabase;
    }

    public boolean isAllowedToUseDatabase() {
        return allowedToUseDatabase;
    }

    public void setColorPlayerOne(PawnColor colorOne) {
        this.player1Color = colorOne;
    }

    public void setColorPlayerTwo(PawnColor colorTwo) {
        this.player2Color = colorTwo;
    }
}
