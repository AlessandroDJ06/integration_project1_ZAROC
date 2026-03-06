package game.integration_project1_zaroc.view.core.profilePictures;

public enum ProfilePictures {
    JAMES("james"),
    BADBUNNY("badbunny"),
    CHAN("chan"),
    JEF("jef"),
    LIAM("liam"),
    MARIA("maria"),
    STEFAN("stefan"),
    EMPTY("empty");

    private final String name;

    ProfilePictures(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public String getPath(){
        return "/game/integration_project1_zaroc/ui/profilePictures/" + this.name + ".png";
    }

}
