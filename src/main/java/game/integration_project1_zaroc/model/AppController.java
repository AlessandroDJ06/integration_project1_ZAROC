package game.integration_project1_zaroc.model;

import game.integration_project1_zaroc.dao.*;
import game.integration_project1_zaroc.model.boardinfo.Peg;
import game.integration_project1_zaroc.model.gameinfo.GameStatus;
import game.integration_project1_zaroc.model.gamelogic.Game;
import game.integration_project1_zaroc.model.gameinfo.GameParticipation;
import game.integration_project1_zaroc.model.gameinfo.PawnColor;
import game.integration_project1_zaroc.model.gamelogic.Move;
import game.integration_project1_zaroc.model.gamelogic.MoveNumber;
import game.integration_project1_zaroc.model.gamelogic.Turn;
import game.integration_project1_zaroc.model.mutliplayer.MultiplayerService;
import game.integration_project1_zaroc.model.players.AIPlayer;
import game.integration_project1_zaroc.model.players.HumanPlayer;
import game.integration_project1_zaroc.model.players.Player;
import game.integration_project1_zaroc.model.selectionslider.PawnColorPickerModel;
import game.integration_project1_zaroc.model.selectionslider.ProfilePicturePickerModel;
import game.integration_project1_zaroc.model.selectionslider.StartingPlayerSelector;
import game.integration_project1_zaroc.model.selectionslider.ThemePickerModel;

import java.util.ArrayList;
import java.util.List;


public class AppController {
    private boolean allowedToUseDatabase;
    private PawnColorPickerModel colorOne;
    private PawnColorPickerModel colorTwo;
    private StartingPlayerSelector startingPlayerSelector;
    private ProfilePicturePickerModel profilePicturePickerModel;
    private ThemePickerModel themePickerModel;
    private GamesDao gamesDao;
    private GameParticipationDao gameParticipationDao;
    private PlayersDao playersDao;
    private UnfinishedGamesDao unfinishedGamesDao;

    private PawnColor player1Color;
    private PawnColor player2Color;

    private Player player1;
    private Player player2;

    private Game game;

    private MultiplayerService multiplayerService;
    private boolean isOnlineMultiplayer;
    private boolean continueInMultiplayer;
    private boolean isHost;
    private boolean continueInLocalPlayer;

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

        this.themePickerModel = new ThemePickerModel();
        this.themePickerModel.setCurrentIndex(0);


        this.game = null;

        this.gamesDao = new GamesDao();
        this.gameParticipationDao = new GameParticipationDao();
        this.playersDao = new PlayersDao();
        this.unfinishedGamesDao = new UnfinishedGamesDao();

        this.multiplayerService = new MultiplayerService(this);
        this.isOnlineMultiplayer = false;
        this.continueInMultiplayer = false;
        this.continueInLocalPlayer = false;

        this.isHost = false;
    }


    public void createAccount(String username, String email, String password, String profilePicture, boolean isPlayerOne) throws ZarocDaoException {
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("email isn't formatted correctly");
        }
        HumanPlayer player = new HumanPlayer(username, email);
        player.setProfilePicture(profilePicture);
        int id = playersDao.createHumanPlayer(player, password);
        player.setPlayerId(id);

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
        if (allowedToUseDatabase && player2 instanceof AIPlayer) {
            try {
                player2.setPlayerId(playersDao.getOrCreateAiPlayer((AIPlayer) player2));
            } catch (ZarocDaoException e) {
                System.out.println("problem: " + e.getMessage());
                if (e.getCause() != null) e.getCause().printStackTrace();
            }
        }

        this.player1Color = PawnColor.values()[colorOne.getCurrentIndex()];
        this.player2Color = PawnColor.values()[colorTwo.getCurrentIndex()];

        this.game = new Game(
                new GameParticipation(this.player1, this.player1Color),
                new GameParticipation(this.player2, this.player2Color)
        );


        this.game.setAllowedSave(allowedToUseDatabase);
        if (allowedToUseDatabase){
            createGameId();
            saveGameParticipations(this.game);
        }

        this.startingPlayerSelector.setPlayer1(player1);
        this.startingPlayerSelector.setPlayer2(player2);

        this.game.getBoard().setupStart((startingPlayerSelector.getPlayers()[startingPlayerSelector.getCurrentIndex()] == player1));
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

    public void resumeGame(int gameId,boolean playerStarts){
        try{
            List<MovesUnfinishedGame> movesUnfinishedGame = unfinishedGamesDao.fetchMovesUnfinishedGame(gameId);
            ArrayList<Turn> turns = new ArrayList<>();
                this.game = new Game(
                        new GameParticipation(player1,this.player1Color),
                        new GameParticipation(player2,this.player2Color)
                );

            boolean hostLayout = true;

            if (!movesUnfinishedGame.isEmpty()) {
                String firstMoveUsername = movesUnfinishedGame.get(0).getUsername();

                if (!firstMoveUsername.equals(player1.getUsername())) {
                    hostLayout = false;
                }
            } else {
                hostLayout = playerStarts;
            }
            this.game.getBoard().setupStart(hostLayout);


            this.game.setGameId(gameId);
            this.game.setStatus(GameStatus.PLAYING);
            gamesDao.updateGame(this.game);
            this.game.setTurns(turns);

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

            int opponentMoves = 0;
            for (Turn turn : turns) {
                Player currentPlayer = turn.getCurrentPlayer();
                if (currentPlayer == null) continue;
                if (!currentPlayer.getUsername().equals(player1.getUsername())) {
                    if (turn.getFirstMove() != null) opponentMoves++;
                    if (turn.getSecondMove() != null) opponentMoves++;
                }
            }
            this.multiplayerService.setLastKnownMoveCount(opponentMoves);



            if (!turns.isEmpty()) {
                Turn lastTurn = turns.getLast();
                if (lastTurn.getSecondMove() != null) {
                    if (isOnlineMultiplayer && continueInMultiplayer) {
                        Player nextPlayer = lastTurn.getCurrentPlayer().getUsername()
                                .equals(player1.getUsername()) ? player2 : player1;
                        this.game.startNewTurn(nextPlayer);
                    } else {
                        this.game.switchCurrentPlayer();
                    }
                } else {
                    this.game.startNewTurn(lastTurn.getCurrentPlayer());
                }
            } else {
                this.game.startNewTurn(player1);
            }
        } catch (ZarocDaoException e) {
            throw new RuntimeException(e);
        }
    }

    public void initOnlineGame(int onlineGameId, Player opponent, PawnColor myColor, PawnColor opponentColor, boolean amIHost) {
        this.isOnlineMultiplayer = true;

        this.player2 = opponent;
        this.player1Color = myColor;
        this.player2Color = opponentColor;

        this.game = new Game(
                new GameParticipation(this.player1, this.player1Color),
                new GameParticipation(this.player2, this.player2Color)
        );

        this.game.setGameId(onlineGameId);
        this.game.getBoard().setupStart(amIHost);
        this.game.setAllowedSave(true);

        if (amIHost && !continueInMultiplayer) {
            saveGameParticipations(this.game);
            Player startingPlayer = amIHost ? this.player1 : this.player2;
            this.game.startNewTurn(startingPlayer);
        } else {
            this.game.setAllowedSave(false);
            this.game.startNewTurn(this.player2);
            this.game.setAllowedSave(true);
        }


    }

    public int getOnlineGameId() {
        if (game == null) {
            throw new IllegalStateException("Geen actief spel gevonden.");
        }
        return game.getGameId();
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

    public ThemePickerModel getThemePickerModel(){return themePickerModel;}

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

    public PawnColor getPlayer2Color() {
        return player2Color;
    }

    public PawnColor getPlayer1Color() {
        return player1Color;
    }

    public MultiplayerService getMultiplayerService() {
        return this.multiplayerService;
    }

    public boolean isOnlineMultiplayer() {
        return isOnlineMultiplayer;
    }

    public void setOnlineMultiplayer(boolean onlineMultiplayer) {
        this.isOnlineMultiplayer = onlineMultiplayer;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        this.isHost = host;
    }

    public int getTotalGamesPlayed() {
        try {
            return playersDao.getTotalGamesPlayed(player1.getPlayerId());
        } catch (ZarocDaoException e) {
            return 0;
        }
    }

    public int getTotalWins() {
        try {
            return playersDao.getTotalWins(player1.getPlayerId());
        } catch (ZarocDaoException e) {
            return 0;
        }
    }

    public String getMostUsedDifficulty() {
        try {
            return playersDao.getMostUsedDifficulty(player1.getPlayerId());
        } catch (ZarocDaoException e) {
            return "Unknown";
        }
    }

    public void setContinueInMultiplayer(boolean continueInMultiplayer) {
        this.continueInMultiplayer = continueInMultiplayer;
    }

    public boolean isContinueInMultiplayer() {
        return continueInMultiplayer;
    }

    public boolean isContinueInLocalPlayer() {
        return continueInLocalPlayer;
    }

    public void setContinueInLocalPlayer(boolean continueInLocalPlayer) {
        this.continueInLocalPlayer = continueInLocalPlayer;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}