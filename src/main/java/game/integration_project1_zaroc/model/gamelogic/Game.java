package game.integration_project1_zaroc.model.gamelogic;

import game.integration_project1_zaroc.model.boardinfo.Board;
import game.integration_project1_zaroc.model.boardinfo.Pawn;
import game.integration_project1_zaroc.model.boardinfo.Peg;
import game.integration_project1_zaroc.model.gameinfo.GameParticipation;
import game.integration_project1_zaroc.model.gameinfo.GameStatus;
import game.integration_project1_zaroc.model.gameinfo.PawnColor;
import game.integration_project1_zaroc.model.players.Player;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private GameStatus status;
    private GameParticipation[] gameParticipations;
    private ArrayList<Turn> turns;

    private Board board;


    public Game(GameParticipation gameParticipation1, GameParticipation gameParticipation2) {
        this.status = GameStatus.PLAYING;
        board = new Board(gameParticipation1.getPawnColor(),gameParticipation2.getPawnColor());
        turns = new ArrayList<>();
        gameParticipations = new GameParticipation[]{gameParticipation1,gameParticipation2};
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
    }

    public Turn getCurrentTurn(){
        if(turns.isEmpty()) return null;
        return turns.get(turns.size()-1);
    }

    public void executeMove(Peg startPeg, Peg destinationPeg) {
        if (status == GameStatus.PLAYING){
            Turn currentTurn = getCurrentTurn();
            MoveNumber moveNumber = (currentTurn.getFirstMove() == null) ? MoveNumber.FIRST_MOVE : MoveNumber.SECOND_MOVE;

            Move newMove = new Move(moveNumber, startPeg, destinationPeg);
            currentTurn.addMove(newMove);

            Pawn upperPawn = startPeg.getUpperPawn();

            startPeg.removePawnFromPeg(upperPawn);
            destinationPeg.addPawnToPeg(upperPawn);
            upperPawn.setCurrentPeg(destinationPeg);

            if (moveNumber == MoveNumber.SECOND_MOVE) {
                switchCurrentPlayer();
            }

            checkWinCondition();
        }


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
                if (allPegs[row][column] != null){
                    Peg destPeg = allPegs[row][column];

                    if (Move.isLegal(startPeg,destPeg)) {

                        Move legalMove = new Move(moveNumber,startPeg, destPeg);

                        legalMoves.add(legalMove);
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
        // Maak de nieuwe game aan
        Game copy = new Game(this.getParticipation1(), this.getParticipation2());
        copy.setStatus(this.getStatus());
        copy.setBoard(this.board.boardCopy());

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
}
