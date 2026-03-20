package game.integration_project1_zaroc.model.gameinfo;

public enum PawnColor {
    WHITE,
    BLACK,
    BLUE,
    RED,
    YELLOW,
    GREEN;
    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
