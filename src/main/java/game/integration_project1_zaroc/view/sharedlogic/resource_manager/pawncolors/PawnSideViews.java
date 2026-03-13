package game.integration_project1_zaroc.view.sharedlogic.resource_manager.pawncolors;

public enum PawnSideViews {
    WHITE("white.png"),
    BLACK("black.png"),
    BLUE("blue.png"),
    RED("red.png"),
    YELLOW("yellow.png"),
    GREEN("green.png")
   ;

    private String path;
    PawnSideViews(String path){
        this.path = path;
    }

    public String getPath() {
        return "/game/integration_project1_zaroc/ui/pawnColors/sideview/"+path;
    }
}
