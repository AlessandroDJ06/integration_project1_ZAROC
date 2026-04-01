package game.integration_project1_zaroc.model.gamelogic;

import game.integration_project1_zaroc.dao.MovesDao;
import game.integration_project1_zaroc.dao.TurnsDao;
import game.integration_project1_zaroc.dao.ZarocDaoException;
import game.integration_project1_zaroc.model.boardinfo.Board;
import game.integration_project1_zaroc.model.boardinfo.Pawn;
import game.integration_project1_zaroc.model.boardinfo.Peg;
import game.integration_project1_zaroc.model.gameinfo.GameParticipation;
import game.integration_project1_zaroc.model.gameinfo.GameStatus;
import game.integration_project1_zaroc.model.gameinfo.PawnColor;
import game.integration_project1_zaroc.model.players.Player;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private GameStatus status;
    private GameParticipation[] gameParticipations;
    private ArrayList<Turn> turns;
    private Move lastMove;
    private Board board;
    private int gameId;
    private TurnsDao turnsDao;
    private MovesDao movesDao;
    private boolean allowedToUseDatabase;
    private boolean allowedSave;
    private Peg startPeg;



    public Game(GameParticipation gameParticipation1, GameParticipation gameParticipation2) {
        this.status = GameStatus.PLAYING;
        board = new Board(gameParticipation1.getPawnColor(),gameParticipation2.getPawnColor());
        turns = new ArrayList<>();
        gameParticipations = new GameParticipation[]{gameParticipation1,gameParticipation2};
        this.lastMove = null;
        this.gameId = -1;
        this.allowedSave = true;
        this.allowedToUseDatabase = true;
        this.turnsDao = new TurnsDao();
        this.movesDao = new MovesDao();
        this.startPeg = null;
    }

    public void switchCurrentPlayer() {
        if (getStatus() == GameStatus.PLAYING) {
            Turn lastTurn = turns.get(turns.size() - 1);

            if (lastTurn.getCurrentPlayer() == getParticipation1().getPlayer()) {
                startNewTurn(getParticipation2().getPlayer());
            } else {
                startNewTurn(getParticipation1().getPlayer());
            }
        }
    }

    public void startNewTurn(Player player){
        Turn turn = new Turn(player);
        turn.setTurnNumber(turns.size() + 1);
        turns.add(turn);
        if (allowedSave && allowedToUseDatabase){
            try {
                turn.setTurnId(turnsDao.saveTurn(gameId,turn));
            } catch (ZarocDaoException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Turn getCurrentTurn(){
        if(turns.isEmpty()) return null;
        return turns.get(turns.size()-1);
    }

    public void selectStartPeg(Peg startPeg){
        this.startPeg = startPeg;
    }

    public void executeMove(Peg destinationPeg) {
        if (status != GameStatus.PLAYING || isUndoMove(this.startPeg, destinationPeg)) {
            return;
        }
        Move newMove = processGameLogic(this.startPeg, destinationPeg);
        saveMoveToDatabase(newMove);
        checkWinCondition();
        this.lastMove = newMove;
    }

    private Move processGameLogic(Peg startPeg, Peg destinationPeg) {
        Turn currentTurn = getCurrentTurn();
        MoveNumber moveNumber = (currentTurn.getFirstMove() == null) ? MoveNumber.FIRST_MOVE : MoveNumber.SECOND_MOVE;

        Move newMove = new Move(moveNumber, startPeg, destinationPeg);
        currentTurn.addMove(newMove);

        Pawn upperPawn = startPeg.getUpperPawn();
        startPeg.removePawnFromPeg(upperPawn);
        destinationPeg.addPawnToPeg(upperPawn);
        upperPawn.setCurrentPeg(destinationPeg);

        newMove.setEndTime(Timestamp.from(Instant.now()));

        if (moveNumber == MoveNumber.SECOND_MOVE) {
            switchCurrentPlayer();
        }
        return newMove;
    }

    private void saveMoveToDatabase(Move newMove) {
        if (allowedSave && (newMove != null) && allowedToUseDatabase) {
            try {
                movesDao.saveMove(getCurrentTurn().getTurnId(), newMove);
            } catch (ZarocDaoException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean isUndoMove(Peg start, Peg dest) {
        if (lastMove == null) return false;

        return start.getXPosition() == lastMove.getDestinationPeg().getXPosition() &&
                start.getYPosition() == lastMove.getDestinationPeg().getYPosition() &&
                dest.getXPosition() == lastMove.getStartPeg().getXPosition() &&
                dest.getYPosition() == lastMove.getStartPeg().getYPosition();
    }


    public void undoMove(Move move) {
        Peg startPeg = move.getStartPeg();
        Peg destPeg = move.getDestinationPeg();

        Pawn upperPawn = startPeg.getUpperPawn();

        destPeg.removePawnFromPeg(upperPawn);
        startPeg.addPawnToPeg(upperPawn);

        Turn currentTurn = getCurrentTurn();

        currentTurn.removeMove(move);

    }

    public List<Move> getLegalMoves(Peg startPeg) {
        List<Move> legalMoves = new ArrayList<>();
        Turn currentTurn = getCurrentTurn();
        MoveNumber moveNumber = (currentTurn.getFirstMove() == null) ? MoveNumber.FIRST_MOVE : MoveNumber.SECOND_MOVE;

        Peg[][] allPegs = board.getAllPegs();

        for (int row = 0; row < allPegs.length; row++) {
            for (int column = 0; column < allPegs[row].length; column++) {
                Peg destPeg = allPegs[row][column];
                if (destPeg != null && Move.isLegal(startPeg, destPeg)) {
                    if (!isUndoMove(startPeg, destPeg)) {
                        legalMoves.add(new Move(moveNumber, startPeg, destPeg));
                    }
                }
            }
        }
        return legalMoves;
    }

    public Player getWinner(){
        if (getParticipation1().getWinner()){
            return getParticipation1().getPlayer();
        } else if (getParticipation2().getWinner()) {
            return getParticipation2().getPlayer();
        } else {
            return null;
        }
    }

    public void checkWinCondition() {
        int countColor1 = 0;
        int countColor2 = 0;

        for (int i = 0; i < board.getAmountOfColumns(); i++) {
            Peg finishPeg = board.getPegPosition(3, i);

            if (finishPeg != null && !finishPeg.getPawns().isEmpty()) {
                PawnColor color = finishPeg.getPawns().getFirst().getPawnColor();
                if (color == gameParticipations[0].getPawnColor()) countColor1++;
                else if (color == gameParticipations[1].getPawnColor()) countColor2++;
            }
        }

        if (countColor1 >= 3) {
            gameParticipations[0].setWinner(true);
            setStatus(GameStatus.ENDED);
        } else if (countColor2 >= 3) {
            gameParticipations[1].setWinner(true);
            setStatus(GameStatus.ENDED);
        }
    }


    public Game gameCopy() {
        Game copy = new Game(this.getParticipation1(), this.getParticipation2());
        copy.setStatus(this.getStatus());
        copy.setBoard(this.board.boardCopy());
        copy.lastMove = this.lastMove;
        copy.setAllowedSave(false);

        ArrayList<Turn> turnsCopy = new ArrayList<>();
        for (Turn originalTurn : this.turns) {
            Turn newTurn = new Turn(originalTurn.getCurrentPlayer());
            newTurn.setTurnNumber(originalTurn.getTurnNumber());
            if (originalTurn.getFirstMove() != null) {
                newTurn.addMove(originalTurn.getFirstMove());
            }
            if (originalTurn.getSecondMove() != null) {
                newTurn.addMove(originalTurn.getSecondMove());
            }
            turnsCopy.add(newTurn);
        }
        copy.setTurns(turnsCopy);
        return copy;
    }

    public void setTurns(ArrayList<Turn> turns) {
        this.turns = turns;
    }
    public List<Turn> getTurns(){
        return turns;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }


    public void setBoard(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public GameParticipation getParticipation1(){
        return this.gameParticipations[0];
    }

    public GameParticipation getParticipation2(){
        return this.gameParticipations[1];
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public GameParticipation[] getGameParticipations() {
        return gameParticipations;
    }

    public void setAllowedSave(boolean allowedSave) {
        this.allowedSave = allowedSave;
    }

    public void setAllowedToUseDatabase(boolean allowedToUseDatabase) {
        this.allowedToUseDatabase = allowedToUseDatabase;
    }
}
