package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.backpropagation;

import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;

public class NegamaxBackupStrategy implements BackpropagationStrategy {

    @Override
    public Void apply(Node leaf, Double value) {
        var current = leaf;
        int leafPlayerID = leaf.getState().getCurrentPlayer();
        while (current != null) {
            if (current.getState().getCurrentPlayer() == leafPlayerID) {
                current.update(value);
            } else {
                current.update(1 - value);
            }
            current =  current.getParent();
        }
        return null;
    }
}
