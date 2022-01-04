package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.selection;

import java.util.List;
import java.util.stream.Collectors;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.nodes.Node;
import at.ac.tuwien.ifs.sge.util.tree.Tree;

public class UCTSelection implements TreePolicy {

    private final double explorationConstant;

    public UCTSelection(double explorationConstant) {
        this.explorationConstant = explorationConstant;
    }

    @Override
    public Tree<Node> apply(Tree<Node> node) {
        Tree<Node> current = node;
        var children = getChildren(node);
        while (!children.isEmpty()) {
            double bestScore = -Double.MAX_VALUE;
            Tree<Node> bestChild = children.get(0);
            for (var child : children) {
                double score = computeUpperConfidenceBound(child.getNode(), current.getNode(), explorationConstant);
                if (score > bestScore) {
                    bestScore = score;
                    bestChild = child;
                }
            }
            current = bestChild;
            children = getChildren(bestChild);
        }
        return current;
    }

    private List<Tree<Node>> getChildren(Tree<Node> node) {
        return node.getChildren().stream()
            .filter(c -> c != null && c.getNode().getState().getPhase() != RiskState.Phase.TERMINATED)
            .collect(Collectors.toList());
    }

    private double computeUpperConfidenceBound(Node node, Node parent, double explorationConstant) {
        if (node.getPlays() < 1) {
            return Double.MAX_VALUE;
        } else {
            double explorationBonus = 2 * Math.sqrt(2 * Math.log(parent.getPlays()) / node.getPlays());
            return node.getValue() / node.getPlays() + explorationConstant * explorationBonus;
        }
    }


}
