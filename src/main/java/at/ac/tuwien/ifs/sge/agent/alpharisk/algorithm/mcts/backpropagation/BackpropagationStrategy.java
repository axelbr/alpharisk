package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.backpropagation;

import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes.Node;
import at.ac.tuwien.ifs.sge.util.tree.Tree;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface BackpropagationStrategy extends BiFunction<Tree<Node>, Double, Void> {
    @Override
    Void apply(Tree<Node> leaf, Double value);
}
