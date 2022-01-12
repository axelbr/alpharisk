package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.Policy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.heuristics.ValueFunction;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.stream.Collectors;

public class HeuristicUCTPolicy extends TreePolicy{

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
