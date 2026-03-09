package game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes;

public enum Themes {
    DEFAULT("default-style","#E2DBC0"),
    DARK_GOLD("dark-gold-style","#202020"),
    DONKEY_KONG("donkey-kong-style","#70B570"),
    PINK("pink-style","#D68FB9");

    private final String folderName;
    private final String color;

    Themes(String folderName,String color) {
        this.folderName = folderName;
        this.color = color;
    }

    public String getPath() {
        return "/game/integration_project1_zaroc/ui/themes/" + folderName + "/";
    }

    public String getColor(){
        return this.color;
    }
}
