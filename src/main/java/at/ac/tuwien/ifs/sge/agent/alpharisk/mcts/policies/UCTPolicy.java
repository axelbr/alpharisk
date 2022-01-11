package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies;

import java.util.stream.Collectors;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public class UCTPolicy implements TreePolicy {

    private final double explorationConstant;

    public UCTPolicy(double explorationConstant) {
        this.explorationConstant = explorationConstant;
    }

    @Override
    public RiskAction selectAction(Node node) {
        double bestScore = -Double.MAX_VALUE;
        RiskAction bestAction = null;
        var nonTerminalNodes = node.expandedChildren().stream()
                .filter(c -> c != null && c.getState().getPhase() != RiskState.Phase.TERMINATED)
                .collect(Collectors.toList());
        for (var child : nonTerminalNodes) {
            double score = computeUpperConfidenceBound(child, explorationConstant);
            if (score > bestScore) {
                bestScore = score;
                bestAction = child.getAction();
            }
        }
        return bestAction;
    }

    private double computeUpperConfidenceBound(Node node, double explorationConstant) {
        if (node.getVisits() < 1) {
            return Double.MAX_VALUE;
        } else {
            double explorationBonus = 2 * Math.sqrt(2 * Math.log(node.getParent().getVisits()) / node.getVisits());
            return node.getValue() + explorationConstant * explorationBonus;
        }
    }


}
