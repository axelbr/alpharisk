package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.expansion;

import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes.Node;
import at.ac.tuwien.ifs.sge.util.tree.Tree;

import java.util.function.Function;

public interface ExpansionStrategy extends Function<Tree<Node>, Tree<Node>> {
}
