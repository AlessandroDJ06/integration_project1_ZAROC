package game.integration_project1_zaroc.view.pages.playervsaiview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.model.players.AIPlayer;
import game.integration_project1_zaroc.model.players.Difficulty;
import game.integration_project1_zaroc.utils.Observer;
import game.integration_project1_zaroc.view.sharedlogic.NavigationService;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.profilePictures.ProfilePictures;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class PlayerVsAiPresenter implements Observer {
    private PlayerVsAiView view;
    private AppController model;
    private Map<Button, AiInfo> aiDataMap = new HashMap<>();

    public PlayerVsAiPresenter(PlayerVsAiView view, AppController model) {
        this.view = view;
        this.model = model;
        view.getResourceManager().addObserver(this);
        initialiseGrid();
        addEventHandlers();
    }

    private void initialiseGrid() {
        addDifficultyRow(0, "EASY",
                new AiInfo(Difficulty.EASY, "ARTHUR", ProfilePictures.ARTHUR, "Often makes random moves."),
                new AiInfo(Difficulty.EASY, "ALISTAIR", ProfilePictures.ALISTAIR, "Plays very cautiously."),
                new AiInfo(Difficulty.EASY, "BEATRICE", ProfilePictures.BEATRICE, "Avoids taking risks.")
        );

        addDifficultyRow(1, "MEDIUM",
                new AiInfo(Difficulty.MEDIUM, "CLARA", ProfilePictures.CLARA, "Starting to recognize patterns."),
                new AiInfo(Difficulty.MEDIUM, "ELEANOR", ProfilePictures.ELEANOR, "Focuses on center control."),
                new AiInfo(Difficulty.MEDIUM, "GIDEON", ProfilePictures.GIDEON, "Slightly aggressive.")
        );

        addDifficultyRow(2, "HARD",
                new AiInfo(Difficulty.HARD, "HELENA", ProfilePictures.HELENA, "Instantly blocks your tactics."),
                new AiInfo(Difficulty.HARD, "IRENE", ProfilePictures.IRENE, "Thinks 3 steps ahead."),
                new AiInfo(Difficulty.HARD, "JAMES", ProfilePictures.JAMES, "Merciless defense.")
        );

        addDifficultyRow(3, "ELITE",
                new AiInfo(Difficulty.ELITE, "LEOPOLD", ProfilePictures.LEOPOLD, "Flawless calculations."),
                new AiInfo(Difficulty.ELITE, "SEBASTIAN", ProfilePictures.SEBASTIAN, "Reads your playstyle in real-time."),
                new AiInfo(Difficulty.ELITE, "STEFAN", ProfilePictures.STEFAN, "The unbeatable grandmaster.")
        );
    }

    private void addDifficultyRow(int row, String label, AiInfo... ais) {
        view.addDifficultyLabel(label, row);
        for (int i = 0; i < ais.length; i++) {
            AiInfo ai = ais[i];
            Button btn = view.addAiButton(ai.profile, i + 1, row);
            aiDataMap.put(btn, ai);

            btn.setOnMouseEntered(e -> {
                view.setButtonStyle(btn, true);
                view.setSelectionText(ai.name, "INFO: " + ai.specialty);
            });

            btn.setOnMouseExited(e -> {
                view.setButtonStyle(btn, false);
                view.setSelectionText("CHOOSE OPPONENT", "Check out their unique playstyle...");
            });

            btn.setOnAction(e -> {
                model.setPlayer2(new AIPlayer(ai.difficulty,ai.name));
                model.getPlayer2().setProfilePicture(ai.name);
                NavigationService.closeWindow(this.view);
            });
        }

        GeneralEventhandlers.addHoverEffect(view.getReturnButton());
        GeneralEventhandlers.addSoundEffect(view.getReturnButton(), view.getResourceManager());
    }

    private void addEventHandlers() {
        view.getReturnButton().setOnAction(e -> {
            NavigationService.closeWindow(this.view);
        });
    }
    @Override
    public void updateLayout(Object args) {
        view.layoutNodes();
    }
}