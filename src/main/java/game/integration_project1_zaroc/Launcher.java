package game.integration_project1_zaroc;

import game.integration_project1_zaroc.model.ai.HeadlessArena;

public class Launcher {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--train")) {
            HeadlessArena.main(args);
        } else {
            Main.main(args);
        }
    }
}