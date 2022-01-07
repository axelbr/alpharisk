package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.backpropagation;

import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;

import java.util.function.BiFunction;

public interface BackpropagationStrategy extends BiFunction<Node, Double, Void> {
    @Override
    Void apply(Node leaf, Double value);
}
