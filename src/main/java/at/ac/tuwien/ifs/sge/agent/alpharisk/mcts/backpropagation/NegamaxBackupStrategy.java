package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.backpropagation;

import at.ac.tuwien.ifs.sge.agent.alpharisk.nodes.Node;
import at.ac.tuwien.ifs.sge.util.tree.Tree;

public class NegamaxBackupStrategy implements BackpropagationStrategy {
    @Override
    public Void apply(Tree<Node> leaf, Double value) {
        Tree<Node> current = leaf;
        int leafPlayerID = leaf.getNode().getState().getCurrentPlayer();
        while (current != null) {
            var node = current.getNode();
            if (node.getState().getCurrentPlayer() == leafPlayerID) {
                node.update(1);
            } else {
                node.update(1 - value);
            }
            current =  current.getParent();
        }
        return null;
    }
}
