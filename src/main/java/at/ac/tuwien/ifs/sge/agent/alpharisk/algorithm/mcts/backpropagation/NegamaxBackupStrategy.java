package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.backpropagation;

import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes.Node;
import at.ac.tuwien.ifs.sge.util.tree.Tree;

public class NegamaxBackupStrategy implements BackpropagationStrategy {
    @Override
    public Void apply(Tree<Node> leaf, Double value) {
        Tree<Node> current = leaf;
        while (current != null) {
            current.getNode().update(value);
            var parent = current.getParent();
            if (parent != null && parent.getNode().getState().getCurrentPlayer() != current.getNode().getState().getCurrentPlayer()) {
                value = 1 - value;
            }
            current = parent;
        }
        return null;
    }
}
