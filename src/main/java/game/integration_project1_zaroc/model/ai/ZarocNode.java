package game.integration_project1_zaroc.model.ai;

import game.integration_project1_zaroc.model.gamelogic.Game;
import game.integration_project1_zaroc.model.gamelogic.Turn;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single node in the Monte Carlo Tree Search (MCTS) structure.
 * Each node stores a specific game state, its parent node, and simulation statistics.
 *
 * @author Alessandro De Jongh
 * @version 1.1
 */
public class ZarocNode {
    private Game state;
    private ZarocNode parent;
    private List<ZarocNode> children;
    private Turn inboundTurn;

    private int visits = 0;
    private double score = 0;

    /**
     * Constructs a new ZarocNode with a given state and parent.
     *
     * @param state       The current {@link Game} state associated with this node.
     * @param parent      The parent node in the search tree.
     * @param inboundTurn The {@link Turn} that led to this state from the parent.
     */
    public ZarocNode(Game state, ZarocNode parent, Turn inboundTurn) {
        this.state = state;
        this.parent = parent;
        this.inboundTurn = inboundTurn;
        this.children = new ArrayList<>();
    }

    /**
     * Calculates the Upper Confidence Bound (UCB1) value for this node.
     * This value is used by the MCTS algorithm to balance exploration and exploitation.
     *
     * @param constant The exploration parameter (typically sqrt(2)).
     * @return The calculated UCB value. Returns {@code Double.MAX_VALUE} if unvisited.
     */
    public double getUCBValue(double constant) {
        if (visits == 0) return Double.MAX_VALUE;
        return (score / visits) + constant * Math.sqrt(Math.log(parent.getVisits()) / visits);
    }

    /** @return The game state associated with this node. */
    public Game getState() { return state; }

    /** @return A list of child nodes. */
    public List<ZarocNode> getChildren() { return children; }

    /** @param child The node to be added as a child of this node. */
    public void addChild(ZarocNode child) { children.add(child); }

    /** @return The number of times this node has been visited during simulations. */
    public int getVisits() { return visits; }

    /** Increments the visit count of this node by one. */
    public void addVisit() { this.visits++; }

    /** @param points The points (win/loss/tie) to be added to the node's total score. */
    public void addScore(double points) { this.score += points; }

    /** @return The turn that led to this node. */
    public Turn getInboundTurn() { return inboundTurn; }

    /** @return The parent node of this node. */
    public ZarocNode getParent() { return parent; }

    /** @return The total accumulated score of this node. */
    public double getScore() { return score; }
}