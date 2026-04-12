package game.integration_project1_zaroc.view.pages.unfinishedgamesview;

import game.integration_project1_zaroc.dao.UnfinishedGame;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.themes.Components;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class UnfinishedGamesView extends BorderPane {
    private ListView<UnfinishedGame> unfinishedGames;
    private ResourceManager resourceManager;


    public UnfinishedGamesView(ResourceManager resourceManager){
        this.resourceManager = resourceManager;
         initialiseNodes();
         layoutNodes();

    }

    private void initialiseNodes(){
        this.unfinishedGames = new ListView<>();
    }

    private void layoutNodes(){
        BorderPane centralContainer = new BorderPane();
        Image boardBackgroundImage = resourceManager.getImage(Components.LARGECONTAINER);
        BackgroundSize backgroundSize = new BackgroundSize(600, 440, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(
                boardBackgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );
        centralContainer.setBackground(new Background(backgroundImage));
        centralContainer.setMaxSize(800, 640);
        setCenter(centralContainer);
        BorderPane.setAlignment(centralContainer, Pos.CENTER);


        Label title = new Label("UNFINISHED GAMES");
        title.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        title.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));

        centralContainer.setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);

        BorderPane.setMargin(title, new Insets(60, 0, 0, 0));


        BackgroundSize backgroundSizeListView = new BackgroundSize(
                100, 100,
                true, true,
                true,
                false
        );

        BackgroundImage backgroundImageListView = new BackgroundImage(
                resourceManager.getImage(Components.PEGVIEW),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSizeListView
        );

        unfinishedGames.setBackground(new Background(backgroundImageListView));

        unfinishedGames.setMaxSize(500,500);
        unfinishedGames.setMinSize(500,500);

        centralContainer.setCenter(unfinishedGames);
        unfinishedGames.setPadding(new Insets(15));


        unfinishedGames.getStylesheets().add(
                "data:text/css," +
                ".list-view .scroll-bar:vertical {" +
                "    -fx-background-color: transparent;" +
                "    -fx-width: 0;" +
                "    -fx-opacity: 0;" +
                "}" +
                ".list-view .scroll-bar:vertical .thumb," +
                ".list-view .scroll-bar:vertical .track {" +
                "    -fx-background-color: transparent;" +
                "}"
        );

        this.setStyle("-fx-background-color: " + this.resourceManager.getTheme().getColor() + ";");
    }

    ListView<UnfinishedGame> getUnfinishedGames() {
        return unfinishedGames;
    }

    ResourceManager getResourceManager() {
        return resourceManager;
    }
}
