package game.integration_project1_zaroc.view.pages.playervsaiview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.model.players.AIPlayer;
import game.integration_project1_zaroc.model.players.Difficulty;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.profilePictures.ProfilePictures;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class PlayerVsAiPresenter {
    private PlayerVsAiView view;
    private AppController model;
    private Map<Button, AiInfo> aiDataMap = new HashMap<>();

    public PlayerVsAiPresenter(PlayerVsAiView view, AppController model) {
        this.view = view;
        this.model = model;
        initialiseGrid();
        addEventHandlers();
    }

    private void initialiseGrid() {
        addDifficultyRow(0, "EASY",
                new AiInfo(Difficulty.EASY,"ARTHUR", ProfilePictures.ARTHUR, "Maakt vaak willekeurige zetten."),
                new AiInfo(Difficulty.EASY,"ALISTAIR", ProfilePictures.ALISTAIR, "Speelt erg voorzichtig."),
                new AiInfo(Difficulty.EASY,"BEATRICE", ProfilePictures.BEATRICE, "Houdt niet van risico's.")
        );

        addDifficultyRow(1, "MEDIUM",
                new AiInfo(Difficulty.MEDIUM,"CLARA", ProfilePictures.CLARA, "Begint patronen te herkennen."),
                new AiInfo(Difficulty.MEDIUM,"ELEANOR", ProfilePictures.ELEANOR, "Focus op centrum controle."),
                new AiInfo(Difficulty.MEDIUM,"GIDEON", ProfilePictures.GIDEON, "Lichtelijk agressief.")
        );

        addDifficultyRow(2, "HARD",
                new AiInfo(Difficulty.HARD,"HELENA", ProfilePictures.HELENA, "Blokkeert direct je tactieken."),
                new AiInfo(Difficulty.HARD,"IRENE", ProfilePictures.IRENE, "Denkt 3 stappen vooruit."),
                new AiInfo(Difficulty.HARD,"JAMES", ProfilePictures.JAMES, "Genadeloze verdediging.")
        );

        addDifficultyRow(3, "ELITE",
                new AiInfo(Difficulty.ELITE,"LEOPOLD", ProfilePictures.LEOPOLD, "Foutloze berekeningen."),
                new AiInfo(Difficulty.ELITE,"SEBASTIAN", ProfilePictures.SEBASTIAN, "Leest je speelstijl real-time."),
                new AiInfo(Difficulty.ELITE,"STEFAN", ProfilePictures.STEFAN, "De onverslaanbare grootmeester.")
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
                view.setSelectionText("KIES EEN TEGENSTANDER", "Bekijk hun unieke speelstijl...");
            });

            btn.setOnAction(e -> {
                System.out.println("Start game tegen: " + ai.name);
                model.setPlayer2(new AIPlayer(ai.difficulty,ai.name));
                model.getPlayer2().setProfilePicture(ai.name);
                closeWindow();
            });
        }
    }

    private void addEventHandlers() {
        view.getReturnButton().setOnAction(e -> {
            closeWindow();
        });
    }

    private void closeWindow(){
        Stage stage = (Stage) view.getScene().getWindow();
        stage.close();
    }

}