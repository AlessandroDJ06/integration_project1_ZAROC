package game.integration_project1_zaroc.model.ai;

import game.integration_project1_zaroc.model.gamelogic.Game;
import game.integration_project1_zaroc.model.gamelogic.Turn;
import java.util.ArrayList;
import java.util.List;

public class ZarocNode {
    private Game state;
    private ZarocNode parent;
    private List<ZarocNode> children;
    private Turn inboundTurn;

    private int visits = 0;
    private double score = 0;

    public ZarocNode(Game state, ZarocNode parent, Turn inboundTurn) {
        this.state = state;
        this.parent = parent;
        this.inboundTurn = inboundTurn;
        this.children = new ArrayList<>();
    }

    public double getUCBValue(double constant) {
        if (visits == 0) return Double.MAX_VALUE;
        return (score / visits) + constant * Math.sqrt(Math.log(parent.getVisits()) / visits);
    }


    public Game getState() { return state; }

    public List<ZarocNode> getChildren() { return children; }

    public void addChild(ZarocNode child) { children.add(child); }

    public int getVisits() { return visits; }

    public void addVisit() { this.visits++; }

    public void addScore(double points) { this.score += points; }

    public Turn getInboundTurn() { return inboundTurn; }

    public ZarocNode getParent() {
        return parent;
    }
}