package game.integration_project1_zaroc.model.gamelogic;

public enum MoveNumber {
    FIRST_MOVE(1),SECOND_MOVE(2);

    private final int number;

    private MoveNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
