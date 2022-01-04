package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.backpropagation;

import at.ac.tuwien.ifs.sge.agent.alpharisk.nodes.Node;
import at.ac.tuwien.ifs.sge.util.tree.Tree;

import java.util.function.BiFunction;

public interface BackpropagationStrategy extends BiFunction<Tree<Node>, Double, Void> {
    @Override
    Void apply(Tree<Node> leaf, Double value);
}
