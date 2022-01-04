package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.backpropagation;

import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes.Node;
import at.ac.tuwien.ifs.sge.util.tree.Tree;

public class NegamaxBackupStrategy implements BackpropagationStrategy {
    @Override
    public Void apply(Tree<Node> leaf, Double value) {
        Tree<Node> current = leaf;
        int leafPlayerID = leaf.getNode().getState().getCurrentPlayer();
        while (current != null) {
            current.getNode().update(current.getNode().getState().getCurrentPlayer() == leafPlayerID?value:1-value);
            current =  current.getParent();
        }
        return null;
    }
}
