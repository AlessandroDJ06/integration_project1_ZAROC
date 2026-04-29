package game.integration_project1_zaroc.model.gamelogic;

import game.integration_project1_zaroc.dao.*;
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
import java.util.Random;

public class Game {
    private GameStatus status;
    private GameParticipation[] gameParticipations;
    private ArrayList<Turn> turns;
    private Move lastMove;
    private Board board;
    private int gameId;
    private TurnsDao turnsDao;
    private MovesDao movesDao;
    private GamesDao gamesDao;
    private GameParticipationDao gameParticipationDao;
    private boolean allowedSave;
    private Peg selectedPeg;
    private Timestamp startTimeMove;


    public Game(GameParticipation gameParticipation1, GameParticipation gameParticipation2) {
        this.status = GameStatus.PLAYING;
        board = new Board(gameParticipation1.getChosenPawnColor(), gameParticipation2.getChosenPawnColor());
        turns = new ArrayList<>();
        gameParticipations = new GameParticipation[]{gameParticipation1, gameParticipation2};
        this.lastMove = null;
        this.gameId = -1;
        this.allowedSave = true;
        this.turnsDao = new TurnsDao();
        this.movesDao = new MovesDao();
        this.gamesDao = new GamesDao();
        this.gameParticipationDao = new GameParticipationDao();
        this.selectedPeg = null;
        this.startTimeMove = Timestamp.from(Instant.now());
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

    public void startNewTurn(Player player) {
        // startUndoTimer();
        Turn turn = new Turn(player);
        turn.setTurnNumber(turns.size() + 1);
        turns.add(turn);
        if (allowedSave) {
            try {
                turn.setTurnId(turnsDao.saveTurn(gameId, turn));
            } catch (ZarocDaoException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Turn getCurrentTurn() {
        if (turns.isEmpty()) return null;
        return turns.get(turns.size() - 1);
    }

    public void selectStartPeg(Peg startPeg) {
        this.selectedPeg = startPeg;
    }

    public void executeMove(Peg destinationPeg) {
        if (status != GameStatus.PLAYING || isUndoMove(this.selectedPeg, destinationPeg)) {
            return;
        }
        Move newMove = processGameLogic(this.selectedPeg, destinationPeg);
        saveMoveToDatabase(newMove);
        checkWinCondition();
        this.lastMove = newMove;
    }

    private Move processGameLogic(Peg startPeg, Peg destinationPeg) {
        Turn currentTurn = getCurrentTurn();
        MoveNumber moveNumber = (currentTurn.getFirstMove() == null) ? MoveNumber.FIRST_MOVE : MoveNumber.SECOND_MOVE;

        Move newMove = new Move(moveNumber, startPeg, destinationPeg);
        newMove.setStartTime(startTimeMove);
        currentTurn.addMove(newMove);

        Pawn upperPawn = startPeg.getUpperPawn();
        startPeg.removePawnFromPeg(upperPawn);
        destinationPeg.addPawnToPeg(upperPawn);
        upperPawn.setCurrentPeg(destinationPeg);

        newMove.setEndTime(Timestamp.from(Instant.now()));
        startTimeMove = Timestamp.from(Instant.now());

        // if (moveNumber == MoveNumber.SECOND_MOVE) {
        //   switchCurrentPlayer();
        //}
        return newMove;
    }

    private void saveMoveToDatabase(Move newMove) {
        if (allowedSave && newMove != null) {
            try {
                movesDao.saveMove(getCurrentTurn().getTurnId(), newMove);
            } catch (ZarocDaoException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void executeMoveUnfinishedGame(Move move) {
        Peg start = board.getPegPosition(move.getStartPeg().getYPosition(), move.getStartPeg().getXPosition());
        Peg dest = board.getPegPosition(move.getDestinationPeg().getYPosition(), move.getDestinationPeg().getXPosition());

        if (start != null && dest != null) {
            turns.getLast().addMove(move);

            Pawn upperPawn = start.getUpperPawn();
            if (upperPawn != null) {
                start.removePawnFromPeg(upperPawn);
                dest.addPawnToPeg(upperPawn);
                upperPawn.setCurrentPeg(dest);
            }

            this.lastMove = move;
        }

        checkWinCondition();
    }

    private boolean isUndoMove(Peg start, Peg dest) {
        if (lastMove == null) return false;

        return start.getXPosition() == lastMove.getDestinationPeg().getXPosition() &&
                start.getYPosition() == lastMove.getDestinationPeg().getYPosition() &&
                dest.getXPosition() == lastMove.getStartPeg().getXPosition() &&
                dest.getYPosition() == lastMove.getStartPeg().getYPosition();
    }


    public void undoMove() {

        Turn currentTurn = getCurrentTurn();

        if (currentTurn.getFirstMove() == null && turns.size() > 1) {
            turns.remove(turns.size() - 1);
            currentTurn = getCurrentTurn();
        }

        Move moveToUndo = null;
        if (currentTurn.getSecondMove() != null) {
            moveToUndo = currentTurn.getSecondMove();
            currentTurn.removeMove(moveToUndo);
        } else if (currentTurn.getFirstMove() != null) {
            moveToUndo = currentTurn.getFirstMove();
            currentTurn.removeMove(moveToUndo);
        }
        Peg startPeg = moveToUndo.getStartPeg();
        Peg destPeg = moveToUndo.getDestinationPeg();

        Pawn upperPawn = destPeg.getUpperPawn();
        destPeg.removePawnFromPeg(upperPawn);
        startPeg.addPawnToPeg(upperPawn);
        upperPawn.setCurrentPeg(startPeg);

        updateLastMoveAfterUndo();

        if (status == GameStatus.ENDED) {
            status = GameStatus.PLAYING;
            getParticipation1().setWinner(false);
            getParticipation2().setWinner(false);
        }
    }

    private void updateLastMoveAfterUndo() {
        if (turns.isEmpty()) {
            this.lastMove = null;
        } else if (getCurrentTurn().getSecondMove() != null) {
            this.lastMove = getCurrentTurn().getSecondMove();
        } else if (getCurrentTurn().getFirstMove() != null) {
            this.lastMove = getCurrentTurn().getFirstMove();
        } else if (turns.size() > 1) {
            this.lastMove = turns.get(turns.size() - 2).getSecondMove();
        } else {
            this.lastMove = null;
        }
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

    public Player getWinner() {
        if (getParticipation1().getWinner()) {
            return getParticipation1().getPlayer();
        } else if (getParticipation2().getWinner()) {
            return getParticipation2().getPlayer();
        } else {
            return null;
        }
    }

    private int countPawnsOnLastRow(PawnColor color) {
        int count = 0;

        for (int i = 0; i < board.getAmountOfColumns(); i++) {
            Peg finishPeg = board.getPegPosition(3, i);

            if (finishPeg != null && !finishPeg.getPawns().isEmpty()) {
                for (Pawn pawn : finishPeg.getPawns()) {
                    if (pawn.getPawnColor() == color) {
                        count++;
                    }
                }
            }
        }
        return count;
    }


    public void checkWinCondition() {
        int countPlayer1 = countPawnsOnLastRow(gameParticipations[0].getChosenPawnColor());
        int countPlayer2 = countPawnsOnLastRow(gameParticipations[1].getChosenPawnColor());

        GameParticipation winner = null;

        if (countPlayer1 >= 3) {
            winner = gameParticipations[0];
        } else if (countPlayer2 >= 3) {
            winner = gameParticipations[1];
        }

        if (winner != null) {
            winner.setWinner(true);
            setStatus(GameStatus.ENDED);
            if (allowedSave) {
                updateGameStatus();
                updateGameParticipation(gameParticipations[0]);
            }

        } else if (countColor2 >= 3) {
            gameParticipations[1].setWinner(true);
            setStatus(GameStatus.ENDED);
            if (allowedSave) {
                updateGameStatus();
                updateGameParticipation(winner);
            }
        }
    }

    public Player getPlayerCloseToWinning() {
        int countPlayer1 = countPawnsOnLastRow(gameParticipations[0].getChosenPawnColor());
        int countPlayer2 = countPawnsOnLastRow(gameParticipations[1].getChosenPawnColor());

        if (countPlayer1 == 2) {
            return gameParticipations[0].getPlayer();
        } else if (countPlayer2 == 2) {
            return gameParticipations[1].getPlayer();
        }

        return null;
    }

    private void updateGameStatus(){
        try{
            gamesDao.updateGame(this);
        } catch (ZarocDaoException e) {
            System.out.println("kon niet opslagen");
        }
    }

    private void updateGameParticipation(GameParticipation gameParticipation) {
        try {
            gameParticipationDao.updateGameParticipationWinner(this, gameParticipation);
        } catch (ZarocDaoException e) {
            System.out.println("kon niet opslagen");
        }
    }


    public Game gameCopy() {
        Game copy = new Game(this.getParticipation1().copy(), this.getParticipation2().copy());
        copy.setStatus(this.getStatus());
        copy.setBoard(this.board.boardCopy());
        copy.setAllowedSave(false);

        if (this.lastMove != null) {
            Peg newStart = copy.getBoard().getPegPosition(this.lastMove.getStartPeg().getYPosition(), this.lastMove.getStartPeg().getXPosition());
            Peg newDest = copy.getBoard().getPegPosition(this.lastMove.getDestinationPeg().getYPosition(), this.lastMove.getDestinationPeg().getXPosition());
            copy.lastMove = new Move(this.lastMove.getMoveNumber(), newStart, newDest);
        }

        ArrayList<Turn> turnsCopy = new ArrayList<>();
        if (!this.turns.isEmpty()) {
            Turn currentOriginalTurn = this.getCurrentTurn();
            Turn newTurn = new Turn(currentOriginalTurn.getCurrentPlayer());
            newTurn.setTurnNumber(currentOriginalTurn.getTurnNumber());

            if (currentOriginalTurn.getFirstMove() != null) {
                Move oldMove = currentOriginalTurn.getFirstMove();
                Peg newStart = copy.getBoard().getPegPosition(oldMove.getStartPeg().getYPosition(), oldMove.getStartPeg().getXPosition());
                Peg newDest = copy.getBoard().getPegPosition(oldMove.getDestinationPeg().getYPosition(), oldMove.getDestinationPeg().getXPosition());
                newTurn.addMove(new Move(MoveNumber.FIRST_MOVE, newStart, newDest));
            }

            if (currentOriginalTurn.getSecondMove() != null) {
                Move oldMove = currentOriginalTurn.getSecondMove();
                Peg newStart = copy.getBoard().getPegPosition(oldMove.getStartPeg().getYPosition(), oldMove.getStartPeg().getXPosition());
                Peg newDest = copy.getBoard().getPegPosition(oldMove.getDestinationPeg().getYPosition(), oldMove.getDestinationPeg().getXPosition());
                newTurn.addMove(new Move(MoveNumber.SECOND_MOVE, newStart, newDest));
            }
            turnsCopy.add(newTurn);
        }
        copy.setTurns(turnsCopy);

        return copy;
    }


    public void executeRandomMove() {
        if (status != GameStatus.PLAYING) {
            return;
        }

        List<Move> allPossibleMoves = new ArrayList<>();
        Peg[][] allPegs = board.getAllPegs();

        for (int row = 0; row < allPegs.length; row++) {
            for (int col = 0; col < allPegs[row].length; col++) {
                Peg currentPeg = allPegs[row][col];
                if (currentPeg != null && !currentPeg.getPawns().isEmpty()) {
                    allPossibleMoves.addAll(getLegalMoves(currentPeg));
                }
            }
        }
        if (!allPossibleMoves.isEmpty()) {
            Random random = new Random();
            Move randomMove = allPossibleMoves.get(random.nextInt(allPossibleMoves.size()));
            selectStartPeg(randomMove.getStartPeg());
            executeMove(randomMove.getDestinationPeg());
        }
    }

    public List<Turn> getTurnsOfPlayer(Player player) {
        return turns.stream().filter(t -> t.getCurrentPlayer().equals(player)).toList();
    }

    public int countMovesInTurns(List<Turn> turns) {
        int count = 0;
        for (Turn turn : turns) {
            if (turn.getFirstMove() != null) count++;
            if (turn.getSecondMove() != null) count++;
        }
        return count;
    }

    public double calculateDurationInMillis(List<Turn> turns) {
        double totalDuration = 0;
        for (Turn turn : turns) {
            totalDuration += getMoveDuration(turn.getFirstMove());
            totalDuration += getMoveDuration(turn.getSecondMove());
        }
        return totalDuration;
    }

    public double getMoveDuration(Move move) {
        if (move != null && move.getStartTime() != null && move.getEndTime() != null) {
            return move.getEndTime().getTime() - move.getStartTime().getTime();
        }
        return 0;
    }

    public boolean isMoveAggressive(Move move) {
        return move != null && (move.getDestinationPeg().getYPosition() - move.getStartPeg().getYPosition() == 1);
    }


    public int countTotalMoves() {
        return countMovesInTurns(this.turns);
    }

    public int countPlayerMoves(Player player) {
        return countMovesInTurns(getTurnsOfPlayer(player));
    }

    public int countPlayerTurns(Player player) {
        return getTurnsOfPlayer(player).size();
    }

    public double calculateGameDuration() {
        return calculateDurationInMillis(this.turns) / 1000;
    }

    public double calculateTotalAvgMoveDuration() {
        int totalMoves = countTotalMoves();
        return calculateDurationInMillis(this.turns) / totalMoves / 1000;
    }

    public double calculateTotalAvgTurnDuration() {
        return turns.isEmpty() ? 0 : calculateDurationInMillis(this.turns) / turns.size() / 1000;
    }

    public double calculatePlayerAvgMoveDuration(Player player) {
        List<Turn> playerTurns = getTurnsOfPlayer(player);
        int totalMoves = countMovesInTurns(playerTurns);
        return totalMoves == 0 ? 0 : calculateDurationInMillis(playerTurns) / totalMoves / 1000;
    }

    public double calculatePlayerAvgTurnDuration(Player player) {
        List<Turn> playerTurns = getTurnsOfPlayer(player);
        return playerTurns.isEmpty() ? 0 : calculateDurationInMillis(playerTurns) / playerTurns.size() / 1000;
    }

    public String calculateGameStyle() {
        int totalMoves = countTotalMoves();
        if (totalMoves == 0) return "Unknown";

        int aggressiveMoves = 0;
        for (Turn turn : turns) {
            if (isMoveAggressive(turn.getFirstMove())) aggressiveMoves++;
            if (isMoveAggressive(turn.getSecondMove())) aggressiveMoves++;
        }

        return ((double) aggressiveMoves / totalMoves) >= 0.5 ? "Aggressive" : "Passive";
    }


    public void setTurns(ArrayList<Turn> turns) {
        this.turns = turns;
    }

    public List<Turn> getTurns() {
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

    public GameParticipation getParticipation1() {
        return this.gameParticipations[0];
    }

    public GameParticipation getParticipation2() {
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

    public boolean isAllowedSave() {
        return allowedSave;
    }

    public Move getLastMove() {
        return lastMove;
    }
}
