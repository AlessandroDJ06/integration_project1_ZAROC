package game.integration_project1_zaroc.model.players;

public enum PlayerStyle {
    DEFAULT,PASSIVE,AGGRESSIVE;

    @Override
    public String toString() {
        return switch (this) {
            case DEFAULT -> "Default";
            case PASSIVE -> "Passief";
            case AGGRESSIVE -> "Agressief";
        };
    }
}
