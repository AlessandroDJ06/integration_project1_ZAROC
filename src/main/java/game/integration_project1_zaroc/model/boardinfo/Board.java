package game.integration_project1_zaroc.model.boardinfo;


import game.integration_project1_zaroc.model.gameinfo.PawnColor;



public class Board {
    private Peg[][] pegPositions;
    private PawnColor pawnColorPlayer1;
    private PawnColor pawnColorPlayer2;

    public Board(PawnColor pawnColorPlayer1 , PawnColor pawnColorPlayer2) {
        this.pegPositions = new Peg[4][10];
        this.pawnColorPlayer1 = pawnColorPlayer1;
        this.pawnColorPlayer2 = pawnColorPlayer2;
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

    public void setupStart(){
        int[] kolommen = {1, 3, 5, 7};
        int row = 0;

        for (int i = 0; i < kolommen.length; i++) {
            int col = kolommen[i];

            for (int laag = 0; laag < 4; laag++) {
                PawnColor kleur;
                if ((i + laag) % 2 == 0) {
                    kleur = this.pawnColorPlayer1;
                } else {
                    kleur = this.pawnColorPlayer2;
                }
                this.pegPositions[row][col].addPawnToPeg(
                        new Pawn(
                                this.pegPositions[0][1],
                                kleur
                        ));
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
        Board boardCopy = new Board(this.pawnColorPlayer1,this.pawnColorPlayer2);

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
