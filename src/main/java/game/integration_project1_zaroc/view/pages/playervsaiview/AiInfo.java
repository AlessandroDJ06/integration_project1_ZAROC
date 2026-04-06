package game.integration_project1_zaroc.view.pages.playervsaiview;
import game.integration_project1_zaroc.model.players.Difficulty;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.profilePictures.ProfilePictures;

public class AiInfo {
    Difficulty difficulty;
    String name;
    ProfilePictures profile;
    String specialty;

    public AiInfo(Difficulty difficulty,String name, ProfilePictures profile, String specialty) {
        this.difficulty = difficulty;
        this.name = name;
        this.profile = profile;
        this.specialty = specialty;
    }


}