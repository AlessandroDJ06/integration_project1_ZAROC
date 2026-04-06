package game.integration_project1_zaroc.view.sharedlogic.resource_manager.profilePictures;

public enum ProfilePictures {
    ALISTAIR("alistair"),
    ARTHUR("arthur"),
    BADBUNNY("badbunny"),
    BEATRICE("beatrice"),
    CHAN("chan"),
    CLARA("clara"),
    ELEANOR("eleanor"),
    EMPTY("empty"),
    GIDEON("gideon"),
    HELENA("helena"),
    IRENE("irene"),
    JAMES("james"),
    JEF("jef"),
    LEOPOLD("leopold"),
    LIAM("liam"),
    MARIA("maria"),
    SEBASTIAN("sebastian"),
    STEFAN("stefan");

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