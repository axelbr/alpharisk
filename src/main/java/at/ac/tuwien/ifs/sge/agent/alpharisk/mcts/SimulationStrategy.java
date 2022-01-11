package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts;

import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.RolloutPolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;

public interface SimulationStrategy {
    double simulate(Node node, RolloutPolicy policy);
}
