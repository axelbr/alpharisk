package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.selection;

import java.util.Collection;
import java.util.stream.Collectors;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;

public class UCTSelection implements TreePolicy {

    private final double explorationConstant;

    public UCTSelection(double explorationConstant) {
        this.explorationConstant = explorationConstant;
    }

    @Override
    public Node apply(Collection<? extends Node> nodes) {
        double bestScore = -Double.MAX_VALUE;
        Node bestChild = null;
        var nonTerminalNodes = nodes.stream()
                .filter(c -> c != null && c.getState().getPhase() != RiskState.Phase.TERMINATED)
                .collect(Collectors.toList());
        for (var child : nonTerminalNodes) {
            double score = computeUpperConfidenceBound(child, explorationConstant);
            if (score > bestScore) {
                bestScore = score;
                bestChild = child;
            }
        }
        return bestChild;
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
