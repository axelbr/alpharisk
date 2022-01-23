package at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.factories.NodeFactory;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.algorithms.DefaultMonteCarloTreeSearch.VALUE;
import static at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.algorithms.DefaultMonteCarloTreeSearch.VISITS;

public abstract class AbstractNode implements Node {

    private final Node parent;
    private final RiskState state;
    private final RiskAction action;
    private final List<Node> children = new ArrayList<>();
    private final NodeStatistics statistics = new NodeStatistics();

    public AbstractNode(Node parent, RiskState state, RiskAction action) {
        this.parent = parent;
        this.state = state;
        this.action = action;
        this.statistics.with(VISITS, 0).with(VALUE,0);
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
        var value = statistics.getDouble(VALUE);
        return getVisits() > 0 ? value / getVisits() : value;
    }

    @Override
    public int getVisits() {
        return statistics.getInt(VISITS);
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
        for(var entry:statistics.entrieSet()){
            this.statistics.increment(entry.getKey(),entry.getValue().doubleValue());
        }
    }

    @Override
    public NodeStatistics getStatistics() {
        return statistics;
    }


    @Override
    public String toString() {
        return getState().getPhase().toString()+"\n("+statistics+")";
    }

}
