package game.integration_project1_zaroc.model.boardinfo.layers;

public enum MaxCapacity {
    FIRST_ROW(4),SECOND_ROW(3),THIRD_ROW(2),FOURTH_ROW(1);

    private final int max;
    private MaxCapacity(int max){
        this.max=max;
    }

    public int getMaxCapacityNumber(){
        return this.max;
    }
}
