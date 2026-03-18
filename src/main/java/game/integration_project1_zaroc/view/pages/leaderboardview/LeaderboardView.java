package game.integration_project1_zaroc.view.pages.leaderboardview;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.fonts.Fonts;
import game.integration_project1_zaroc.view.sharedlogic.utils.LayoutHelpers;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class LeaderboardView extends BorderPane {
    private ResourceManager resourceManager;
    private TextArea leaderboard;
    private ComboBox sortOptionsDropdown;
    private ToggleButton ascending;
    private ToggleButton descending;
    private ToggleGroup displayOrder;

    public LeaderboardView(ResourceManager resourceManager){
        this.resourceManager = resourceManager;
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes(){
        this.ascending = new ToggleButton("ascending");
        this.descending = new ToggleButton("descending");
        this.displayOrder = new ToggleGroup();
        this.leaderboard = new TextArea("leaderboard");
        this.sortOptionsDropdown = new ComboBox<>();
    }

    private void layoutNodes(){
        setBackground(new Background(LayoutHelpers.setBackground(this.resourceManager,550,650)));
        this.setMinSize(550,650);

        //center container
        BorderPane center = new BorderPane();
        setCenter(center);
        BorderPane.setAlignment(center,Pos.CENTER);

        //title
        Label title = new Label("Leaderboard");
        center.setPadding(new Insets(80, 20, 20, 20));
        title.setFont(resourceManager.getFont(Fonts.PRESSSTART2PTITLE));
        title.setTextFill(Color.web(resourceManager.getTheme().getTextColor()));
        center.setTop(title);
        BorderPane.setAlignment(title, Pos.TOP_CENTER);


    }





}
