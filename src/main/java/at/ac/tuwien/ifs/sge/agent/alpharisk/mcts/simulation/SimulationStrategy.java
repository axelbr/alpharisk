package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.simulation;

import at.ac.tuwien.ifs.sge.agent.alpharisk.nodes.Node;
import at.ac.tuwien.ifs.sge.util.tree.Tree;

import java.util.function.Function;

public interface SimulationStrategy extends Function<Tree<Node>, Double> {

}
