package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.simulation;

import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;

import java.util.function.Function;

public interface SimulationStrategy extends Function<Node, Double> {

}
