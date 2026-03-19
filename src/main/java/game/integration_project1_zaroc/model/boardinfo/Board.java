package game.integration_project1_zaroc.model.boardinfo;


public class Board {
    private Peg[][] pegPositions;

    public Board() {
        this.pegPositions = new Peg[4][10];
        createPegs();

    }

    private void createPegs() {

        for (int row = 0; row < pegPositions.length; row++) {
            for (int column = 0; column < pegPositions[row].length; column++) {
                boolean shouldAdd = (row < 2 && column % 2 != 0 && column != 9)
                        || (row >= 2 && column % 2 == 0);

                if (shouldAdd) {
                    pegPositions[row][column] = new Peg(column, row);

                }
            }
        }
    }

    public Peg getPegPosition(int row, int column) {
        return pegPositions[row][column];
    }

    public void setPegPosition(Peg peg, int row, int column) {
        pegPositions[row][column]=peg;
    }

    public int getAmountOfRows() {
        return pegPositions.length;
    }

    public Peg[][] getAllPegs() {
        return pegPositions;
    }

    public int getAmountOfColumns(){
        return pegPositions[10].length;
    }

    public Board boardCopy() {
        Board boardCopy = new Board();

        for (int row = 0; row < getAmountOfRows(); row++) {
            for (int column = 0; column < getAmountOfColumns(); column++) {

                Peg oldPeg = getPegPosition(row, column);
                Peg newPeg = getPegPosition(row, column);

                for (Pawn oldPawn : oldPeg.getPawns()) {
                    Pawn newPawn = new Pawn(newPeg);
                    newPeg.addPawnToPeg(newPawn);
                }
            }
        }
        return boardCopy;
    }
}
