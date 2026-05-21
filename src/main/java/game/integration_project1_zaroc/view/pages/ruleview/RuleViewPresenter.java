package game.integration_project1_zaroc.view.pages.ruleview;

import game.integration_project1_zaroc.model.AppController;
import game.integration_project1_zaroc.utils.Observer;
import game.integration_project1_zaroc.view.sharedlogic.NavigationService;
import game.integration_project1_zaroc.view.sharedlogic.utils.GeneralEventhandlers;

/**
 * Presenter for the rules screen.
 * Handles navigation back to the previous screen.
 */
public class RuleViewPresenter implements Observer {
    /** The rule view this presenter manages.*/
    private final RuleView view;
    /** The connection to the model.*/
    private final AppController model;

    /**
     * Creates a new RuleViewPresenter and registers event handlers.
     *
     * @param ruleView the rules view to manage.
     * @param model    the connection to the model.
     */
    public RuleViewPresenter(RuleView ruleView, AppController model) {
        this.view = ruleView;
        this.model = model;
        view.getResourceManager().addObserver(this);
        addEventHandlers();

    }
    /**
     * Registers event handlers for the rules view controls.
     * Contains a close action, hover effect, and sound effect.
     */
    private void addEventHandlers(){
        view.getReturnButton().setOnAction(actionEvent ->{
            NavigationService.closeWindow(this.view);
        });
        GeneralEventhandlers.addHoverEffect(view.getReturnButton());
        GeneralEventhandlers.addSoundEffect(view.getReturnButton(), view.getResourceManager());
    }
    /**
     * Called when the resource manager notifies observers of a layout change.
     * Triggers a full layout refresh on the view.
     */
    @Override
    public void updateLayout(Object args) {
        view.layoutNodes();
    }
}
