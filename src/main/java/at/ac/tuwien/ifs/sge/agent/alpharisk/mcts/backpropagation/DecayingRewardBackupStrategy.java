package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.backpropagation;

import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DecayingRewardBackupStrategy implements BackpropagationStrategy {

    private final double decay;

    public DecayingRewardBackupStrategy(double decay) {
        this.decay = decay;
    }

    @Override
    public Void apply(Node leaf, Double value) {
        var current = leaf;
        int leafPlayerID = leaf.getState().getCurrentPlayer();
        double discountFactor = decay;
        while (current != null) {
            if (current.getState().getCurrentPlayer() == leafPlayerID) {
                current.update(value * discountFactor);
            } else {
                current.update((1 - value) * discountFactor);
            }
            current =  current.getParent();
            discountFactor = decay * discountFactor;
        }
        return null;
    }
}
