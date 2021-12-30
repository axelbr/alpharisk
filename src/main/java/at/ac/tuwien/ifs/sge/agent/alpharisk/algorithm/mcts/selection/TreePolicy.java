package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.selection;

import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes.Node;
import at.ac.tuwien.ifs.sge.util.tree.Tree;

import java.util.function.Function;

@FunctionalInterface
public interface TreePolicy extends Function<Tree<Node>, Tree<Node>> {
}
