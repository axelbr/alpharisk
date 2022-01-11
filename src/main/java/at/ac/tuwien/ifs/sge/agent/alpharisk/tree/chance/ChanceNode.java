package at.ac.tuwien.ifs.sge.agent.alpharisk.tree.chance;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.AbstractNode;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.decision.DefaultDecisionNode;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public abstract class ChanceNode extends AbstractNode {

    private List<Pair<Node, Double>> children = new ArrayList<>();
    private int visits;

    public ChanceNode(Node parent, RiskState state, RiskAction action) {
        super(parent, state, action);
    }

    public abstract double getProbability(RiskState outcome);
    protected abstract boolean isAlreadySampled(Node node);
    public abstract Node sampleOutcome();

    @Override
    public Node select(RiskAction action) {
        if (isFullyExpanded()) {
            var pmf = new EnumeratedDistribution<>(children);
            return pmf.sample();
        } else {
            var node = sampleOutcome();
            if (!isAlreadySampled(node)) {
                addChild(node);
            }
            return node;
        }
    }

    @Override
    public Node expand(RiskAction action) {
        return select(null);
    }

    @Override
    public double getValue() {
        return children.stream()
                .map(c -> c.getValue() * c.getKey().getValue())
                .reduce(0.0, Double::sum);
    }

    @Override
    public void addChild(Node node) {
        var child = Pair.create(node, getProbability(node.getState()));
        children.add(child);
    }

    @Override
    public Collection<? extends Node> expandedChildren() {
        return children.stream().map(Pair::getKey).collect(Collectors.toList());
    }

    @Override
    public Set<RiskAction> expandedActions() {
        return Set.of(getAction());
    }

    @Override
    public boolean isLeaf() {
        return false;
    }



    @Override
    public String toString() {
        return "Chance" + super.toString();
    }

    @Override
    public int getVisits() {
        return visits;
    }

    @Override
    public void update(double value) {
        visits += 1;
        getParent().update(getValue());
    }
}
