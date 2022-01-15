package at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.factories.NodeFactory;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractNode implements Node {

    private final Node parent;
    private final RiskState state;
    private final RiskAction action;
    private final List<Node> children = new ArrayList<>();
    private double value;
    private int visits;

    public AbstractNode(Node parent, RiskState state, RiskAction action) {
        this.parent = parent;
        this.state = state;
        this.action = action;
    }

    @Override
    public RiskAction getAction() {
        return action;
    }

    @Override
    public RiskState getState() {
        return state;
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public Collection<? extends Node> expandedChildren() {
        return children;
    }

    @Override
    public double getValue() {
        return visits > 0 ? value / visits : value;
    }

    @Override
    public int getVisits() {
        return visits;
    }

    @Override
    public int size() {
        return 1 + children.stream().map(Node::size).reduce(0, Integer::sum);
    }

    @Override
    public void addChild(Node node) {
        children.add(node);
    }

    @Override
    public void update(NodeStatistics statistics) {
        this.visits += 1;
        this.value += statistics.getDouble("value");
    }

    @Override
    public NodeStatistics getStatistics() {
        return NodeStatistics.of("value", value).concat("visits", visits);
    }

    @Override
    public String toString() {
        return getState().getPhase().toString();
    }

}
