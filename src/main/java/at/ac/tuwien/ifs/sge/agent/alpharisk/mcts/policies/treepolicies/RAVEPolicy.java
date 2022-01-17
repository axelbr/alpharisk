package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.treepolicies;

import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.ValueFunction;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.Node;

import static at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.algorithms.DefaultMonteCarloTreeSearch.VALUE;
import static at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.algorithms.DefaultMonteCarloTreeSearch.VISITS;
import static at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.algorithms.rave.RapidActionValueEstimationSearch.*;

public class RAVEPolicy extends TreePolicy {

    private final double explorationConstant;
    private final ValueFunction stateHeuristic;

    public RAVEPolicy(double explorationConstant) {
        this(explorationConstant, s -> 0.0);
    }

    public RAVEPolicy(double explorationConstant, ValueFunction stateHeuristic) {
        this.explorationConstant = explorationConstant;
        this.stateHeuristic = stateHeuristic;
    }

    @Override
    public double computeScore(Node node) {
        if (node.getVisits() < 1) {
            return Double.MAX_VALUE;
        } else {
            double explorationBonus = 2 * Math.sqrt(2 * Math.log(node.getParent().getVisits()) / node.getVisits());
            double heuristicBonus = stateHeuristic.evaluate(node.getState()) / node.getVisits();
            return amaf_value(node) + explorationConstant * explorationBonus + heuristicBonus;
        }
    }

    public double amaf_value(Node n) {
        var stats = n.getStatistics();
        int amafVisits = Math.max(1, stats.getInt(AMAF_VISITS));
        int visits = Math.max(1, stats.getInt(VISITS));
        double amafValue = stats.getDouble(AMAF_VALUE);
        double value = stats.getDouble(VALUE);
        double beta = (double) amafVisits /
                      (visits + amafVisits + 4 * visits * amafVisits * stats.getDouble(BIAS) * stats.getDouble(BIAS));
        return beta * (amafValue / amafVisits) + (1 - beta) * (value / visits);
    }
}
