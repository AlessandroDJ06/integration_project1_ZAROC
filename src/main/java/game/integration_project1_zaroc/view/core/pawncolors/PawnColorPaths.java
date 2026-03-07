package game.integration_project1_zaroc.view.core.pawncolors;

public enum PawnColorPaths{
    BLACK("black.png"),
    BROWN("brown.png"),
    DARKBLUE("dark_blue.png"),
    GREEN("green.png"),
    LIGHTBLUE("light_blue.png"),
    WHITE("white.png");

    private String path;

    PawnColorPaths(String path){
        this.path = path;
    }

    public String getPath() {
        return "/game/integration_project1_zaroc/ui/pawnColors/colors/" + path;
    }
}
