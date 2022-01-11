package at.ac.tuwien.ifs.sge.agent.alpharisk.tree;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
    public void update(double value) {
        this.value += value;
        visits += 1;
        if (parent != null) {
            if (parent.getState().getCurrentPlayer() != state.getCurrentPlayer()) {
                parent.update(1.0 - value);
            } else {
                parent.update(value);
            }
        }
    }

    @Override
    public String toString() {
        return getState().getPhase().toString();
    }

}
