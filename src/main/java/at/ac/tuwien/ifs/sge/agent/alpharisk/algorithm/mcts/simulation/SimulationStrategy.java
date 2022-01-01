package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.simulation;

import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes.Node;
import at.ac.tuwien.ifs.sge.util.tree.Tree;

import java.util.function.Function;

public interface SimulationStrategy extends Function<Tree<Node>, Double> {

}
