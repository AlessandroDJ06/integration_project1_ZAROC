package game.integration_project1_zaroc.model.ai;

import game.integration_project1_zaroc.model.gamelogic.Game;
import game.integration_project1_zaroc.model.gamelogic.Turn;
import java.util.ArrayList;
import java.util.List;

public class ZarocNode {
    private Game state;               // De kopie van het spel in deze node
    private ZarocNode parent;         // De node waar we vandaan kwamen
    private List<ZarocNode> children; // Mogelijke vervolg-beurten
    private Turn inboundTurn;         // De beurt die tot deze node leidde

    private int visits = 0;           // n_i: Hoe vaak bezocht?
    private double score = 0;         // w_i: Aantal winstpunten (1 voor winst, 0.5 gelijk, 0 verlies)

    public ZarocNode(Game state, ZarocNode parent, Turn inboundTurn) {
        this.state = state;
        this.parent = parent;
        this.inboundTurn = inboundTurn;
        this.children = new ArrayList<>();
    }

    // De beroemde UCB1 formule om de beste node te kiezen
    public double getUCBValue() {
        if (visits == 0) return Double.MAX_VALUE; // Geef onbekende paden altijd voorrang

        // Formule: Gemiddelde score + Exploratie-factor
        return (score / visits) + 1.41 * Math.sqrt(Math.log(parent.getVisits()) / visits);
    }

    // Getters en setters
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