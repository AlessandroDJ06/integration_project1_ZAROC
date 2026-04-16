package game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts;

public enum Fonts {
    PRESSSTART2PTITLE("/game/integration_project1_zaroc/ui/fonts/PressStart2P-Regular.ttf",20),
    PRESSSTART2PTITLEBIG("/game/integration_project1_zaroc/ui/fonts/PressStart2P-Regular.ttf",30),
    PRESSSTART2PSLIDER("/game/integration_project1_zaroc/ui/fonts/PressStart2P-Regular.ttf",12),
    PRESSSTART2PLARGE("/game/integration_project1_zaroc/ui/fonts/PressStart2P-Regular.ttf",15),
    PRESSSTART2BUTTONSTYLE("/game/integration_project1_zaroc/ui/fonts/PressStart2P-Regular.ttf",40),
    PRESSSTART2PSMALL("/game/integration_project1_zaroc/ui/fonts/PressStart2P-Regular.ttf",10),
    PRESSSTART2PMEDIUM("/game/integration_project1_zaroc/ui/fonts/PressStart2P-Regular.ttf",12);

    private String path;
    private double weight;
     Fonts(String path, double weight){
         this.path = path;
         this.weight = weight;
     }

    public String getPath() {
        return path;
    }

    public double getWeight() {
        return weight;
    }
}
