package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.treepolicies;

import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.ValueFunction;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;

public class HeuristicUCTPolicy extends TreePolicy {

    private final double explorationConstant;
    private final ValueFunction stateHeuristic;

    public HeuristicUCTPolicy(double explorationConstant) {
        this(explorationConstant, s -> 0.0);
    }

    public HeuristicUCTPolicy(double explorationConstant, ValueFunction stateHeuristic) {
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
            return node.getValue() + explorationConstant * explorationBonus + heuristicBonus;
        }
    }
}
