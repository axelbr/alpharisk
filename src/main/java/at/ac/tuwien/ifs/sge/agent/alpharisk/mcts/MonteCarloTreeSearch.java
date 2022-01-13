package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts;

import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;

public interface MonteCarloTreeSearch<S, A> {
    A getBestAction(Node node);
    void runIteration(Node root);
    Node select(Node node);
    Node expand(Node node);
    double rollout(S state);
    void backup(Node node, double value);
}
