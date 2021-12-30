package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.rollouts;

import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes.Node;
import at.ac.tuwien.ifs.sge.util.tree.Tree;

import java.util.function.Function;

public interface RolloutPolicy extends Function<Tree<Node>, Double> {

}
