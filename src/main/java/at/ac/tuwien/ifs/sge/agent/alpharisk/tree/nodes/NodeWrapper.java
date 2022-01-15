package at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.RiskState;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public abstract class NodeWrapper implements Node {
    private final Node node;

    public Node unwrapped() {
        return node;
    }

    public NodeWrapper(Node node) {
        this.node = node;
    }

    @Override
    public RiskAction getAction() {
        return node.getAction();
    }

    @Override
    public RiskState getState() {
        return node.getState();
    }

    @Override
    public Node getParent() {
        return node.getParent();
    }

    @Override
    public double getValue() {
        return 0;
    }

    @Override
    public int getVisits() {
        return node.getVisits();
    }

    @Override
    public Collection<? extends Node> expandedChildren() {
        return node.expandedChildren();
    }

    @Override
    public Set<RiskAction> expandedActions() {
        return node.expandedActions();
    }

    @Override
    public int size() {
        return node.size();
    }

    @Override
    public Node select(RiskAction action) {
        return node.select(action);
    }

    @Override
    public Node expand(RiskAction action) {
        return node.expand(action);
    }

    @Override
    public void update(NodeStatistics statistics) {
        node.update(statistics);
    }

    @Override
    public NodeStatistics getStatistics() {
        return node.getStatistics();
    }

    @Override
    public void addChild(Node node) {
        node.addChild(node);
    }

    @Override
    public boolean isLeaf() {
        return node.isLeaf();
    }

    @Override
    public boolean isFullyExpanded() {
        return node.isFullyExpanded();
    }

    @Override
    public boolean isRoot() {
        return node.isRoot();
    }
}
