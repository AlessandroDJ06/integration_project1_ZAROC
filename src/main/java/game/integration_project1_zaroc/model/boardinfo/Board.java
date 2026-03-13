package game.integration_project1_zaroc.model.boardinfo;

public class Board {
    private Peg[][] pegPositions;

    public Board() {
        this.pegPositions = new Peg[10][4];
        createPegs();

    }

    private void createPegs() {

        for (int row = 0; row < pegPositions.length; row++) {
            for (int column = 0; column < pegPositions[row].length; column++) {
                boolean shouldAdd = (row < 2 && column % 2 != 0 && column != 9)
                        || (row >= 2 && column % 2 == 0);

                if (shouldAdd) {
                    pegPositions[row][column] = new Peg(row,column);

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
}
