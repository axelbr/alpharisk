package at.ac.tuwien.ifs.sge.agent.alpharisk.tree.chance;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.AbstractNode;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public abstract class ChanceNode extends AbstractNode {

    private List<Pair<Node, Double>> children = new ArrayList<>();

    public ChanceNode(Node parent, RiskState state, RiskAction action) {
        super(parent, state, action);
    }

    public abstract double getProbability(RiskState outcome);

    @Override
    public Node select() {
        var pairs = getChildren().stream()
                .map(c -> Pair.create(c, getProbability(c.getState())))
                .collect(Collectors.toList());
        var pmf = new EnumeratedDistribution<>(pairs);
        var node = pmf.sample();
        return node;
    }

    @Override
    public Optional<? extends Node> select(RiskAction action) {
        return Optional.of(select());
    }

    @Override
    public Node expand() {
        return select();
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
    public Collection<? extends Node> getChildren() {
        return children.stream().map(Pair::getKey).collect(Collectors.toList());
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public final boolean isFullyExpanded() {
        return true;
    }
}
