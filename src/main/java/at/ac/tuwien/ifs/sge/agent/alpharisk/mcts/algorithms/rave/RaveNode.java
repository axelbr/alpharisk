package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.algorithms.rave;

import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.factories.NodeFactory;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.NodeStatistics;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.NodeWrapper;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import org.apache.commons.configuration2.BaseConfiguration;

import java.util.function.Function;

public class RaveNode extends NodeWrapper {

    private double value, amafValue, bias;
    private int visits, amafVisits;
    public final static String VISITS = "visits";
    public final static String VALUE = "value";
    public final static String AMAF_VISITS = "amaf_visits";
    public final static String AMAF_VALUE = "amaf_value";

    public RaveNode(Node node, double bias) {
        super(node);
        this.bias = bias;
    }

    @Override
    public void update(NodeStatistics statistics) {
        if (statistics.contains(AMAF_VALUE)) {
            double value = statistics.getDouble(AMAF_VALUE);
            amafVisits += 1;
            amafValue += (value - amafValue) / amafVisits;
        }

        if (statistics.contains(VALUE)) {
            double value = statistics.getDouble(VALUE);
            visits += 1;
            this.value += (value - this.value) / visits;
        }
    }

    @Override
    public NodeStatistics getStatistics() {
        return NodeStatistics.of(VALUE, value)
                .with(VISITS, visits)
                .with(AMAF_VALUE, amafValue)
                .with(AMAF_VISITS, amafVisits);
    }

    @Override
    public int getVisits() {
        return visits;
    }

    @Override
    public double getValue() {
        double beta = beta();
        return beta * (amafValue / amafVisits) + (1 - beta) * (value / visits);
    }

    private double beta() {
        int amafVisits = Math.max(1, this.amafVisits);
        int visits = Math.max(1, this.visits);
        return (double) amafVisits / (visits + amafVisits + 4 * visits * amafVisits * bias * bias);
    }
}
