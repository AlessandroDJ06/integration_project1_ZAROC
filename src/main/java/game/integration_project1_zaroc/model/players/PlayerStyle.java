package game.integration_project1_zaroc.model.players;

public enum PlayerStyle {
    PASSIVE,AGGRESSIVE;

    @Override
    public String toString() {
        return switch (this) {
            case PASSIVE -> "Passief";
            case AGGRESSIVE -> "Agressief";
        };
    }
}
