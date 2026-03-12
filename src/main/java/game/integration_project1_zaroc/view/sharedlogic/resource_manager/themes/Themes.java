package game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes;

public enum Themes {
    DEFAULT("default-style","#E2DBC0","#4D4730"),
    DARK_GOLD("dark-gold-style","#202020","#F4E4BC"),
    DONKEY_KONG("donkey-kong-style","#70B570","#1A3D1A"),
    PINK("pink-style","#FFB7C5","#FEEFFF");

    private final String folderName;
    private final String color;
    private final String textColor;

    Themes(String folderName,String color,String textColor) {
        this.folderName = folderName;
        this.color = color;
        this.textColor = textColor;
    }

    public String getPath() {
        return "/game/integration_project1_zaroc/ui/themes/" + folderName + "/";
    }

    public String getColor(){
        return this.color;
    }

    public String getTextColor(){
        return this.textColor;
    }
}
