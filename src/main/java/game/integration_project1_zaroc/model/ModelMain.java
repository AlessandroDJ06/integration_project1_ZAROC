package game.integration_project1_zaroc.model;

import game.integration_project1_zaroc.model.boardinfo.Board;
import game.integration_project1_zaroc.model.gameinfo.PawnColor;

public class ModelMain {
    public static void main(String[] args) {
        Board board = new Board(PawnColor.BLACK,PawnColor.BLUE);
    }
}
