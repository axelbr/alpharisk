package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.selection;

import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes.Node;
import at.ac.tuwien.ifs.sge.util.tree.Tree;

import java.util.Comparator;

public class UCTSelection implements TreePolicy {

    private final double explorationConstant;

    public UCTSelection(double explorationConstant) {
        this.explorationConstant = explorationConstant;
    }

    @Override
    public Tree<Node> apply(Tree<Node> node) {
        while (!node.isLeaf()) {
            Tree<Node> finalNode = node;
            node = node.getChildren().stream()
                    .max(Comparator.comparingDouble(a -> computeUpperConfidenceBound(a.getNode(), finalNode.getNode(), explorationConstant)))
                    .orElseThrow();
        }
        return node;
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
