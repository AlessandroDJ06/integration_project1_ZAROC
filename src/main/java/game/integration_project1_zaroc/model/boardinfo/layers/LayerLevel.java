package game.integration_project1_zaroc.model.boardinfo.layers;

public enum LayerLevel {
    LEVEL_ONE(1),LEVEL_TWO(2),LEVEL_THREE(3),LEVEL_FOUR(4);

    private int number;

    private LayerLevel(int number){
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

}
