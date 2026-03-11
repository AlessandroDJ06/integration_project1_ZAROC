package game.integration_project1_zaroc.view.pages.gamesetupview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.model.pawncolorpicker.PawnColorPickerModel;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.ResourceManager;
import game.integration_project1_zaroc.view.sharedlogic.resource_manager.pawncolors.PawnColorPaths;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;

import java.util.Arrays;
import java.util.List;

public class GameSetupPresenter {
    private AppController model;
    private GameSetupView view;
    private List<Button> buttons;
    private GeneralEventhandlers generalEventhandlers;
    private PawnColorPickerModel colorOne;
    private PawnColorPickerModel colorTwo;

    public GameSetupPresenter(GameSetupView view , AppController appController){
        this.view = view;
        this.model = appController;
        this.buttons = Arrays.asList(view.getProfileButton(),view.getSettingsButton(),view.getInfoButton(),view.getColorPickerOne().getLeftButton(),view.getColorPickerOne().getRightButton(),view.getColorPickerTwo().getLeftButton(),view.getColorPickerTwo().getRightButton());
        this.generalEventhandlers = new GeneralEventhandlers();
        this.colorOne = new PawnColorPickerModel(0);
        this.colorTwo = new PawnColorPickerModel(1);
        colorOne.setCurrentIndexOtherPicker(colorTwo.getCurrentIndexOtherPicker());
        colorTwo.setCurrentIndexOtherPicker(colorOne.getCurrentIndexOtherPicker());
        addEventHandlers();
        updateView();
    }

    private void addEventHandlers(){
        for (Button button : buttons){
            generalEventhandlers.addHoverEffect(button);
        }

        view.getColorPickerOne().getLeftButton().setOnAction(event -> {
            colorOne.decreaseCurrentIndex();
            colorTwo.setCurrentIndexOtherPicker(colorOne.getCurrentIndex());
            updateView();
        });

        view.getColorPickerTwo().getLeftButton().setOnAction(event -> {
            colorTwo.decreaseCurrentIndex();
            colorOne.setCurrentIndexOtherPicker(colorTwo.getCurrentIndex());
            updateView();
        });

        view.getColorPickerOne().getRightButton().setOnAction(event -> {
            colorOne.increaseCurrentIndex();
            colorTwo.setCurrentIndexOtherPicker(colorOne.getCurrentIndex());
            updateView();
        });

        view.getColorPickerTwo().getRightButton().setOnAction(event -> {
            colorTwo.increaseCurrentIndex();
            colorOne.setCurrentIndexOtherPicker(colorTwo.getCurrentIndex());
            updateView();
        });
    }

    private void updateView(){
        view.getColorPickerOne().getPawnColor().setImage(
                view.getResourceManager().getPawnColor(PawnColorPaths.values()[colorOne.getCurrentIndex()])
        );
        view.getColorPickerTwo().getPawnColor().setImage(
                view.getResourceManager().getPawnColor(PawnColorPaths.values()[colorTwo.getCurrentIndex()])
        );
    }
}
