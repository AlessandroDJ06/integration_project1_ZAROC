package game.integration_project1_zaroc.view.core.themes;

public enum Components {
    BOARD("board.png"),
    PEG("peg.png"),
    FINISH("finish.png"),
    PROFILE("profile-button.png"),
    RULES("rules-button.png"),
    SETTINGS("settings-button.png"),
    UNDO("undo-button.png"),
    UNIVERSAL("universal-button.png"),
    GAMEPLAYERS("gameParticipation.png"),
    PEGVIEW("pegview.png"),
    POLECAPAFOUR("pole_capa_4.png"),
    POLECAPATHREE("pole_capa_3.png"),
    POLECAPATWO("pole_capa_2.png");

    private final String fileName;

    Components(String fileName) {
        this.fileName = fileName;
    }
    /**
     *
     * @param theme geef het theme mee van de image router zodat je het juist component ophaalt
     * @return het pad naar de gekozen component graphics
     */
    public String getPath(Themes theme) {
        return theme.getPath() + this.fileName;
    }
}