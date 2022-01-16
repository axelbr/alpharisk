package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts;

import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.Node;
import at.ac.tuwien.ifs.sge.util.pair.Pair;

import java.util.List;
import java.util.function.Function;

public interface MonteCarloTreeSearch<S, A> {
    A getBestAction(Node node);
    void runIteration(Node root);
    Node select(Node node);
    Node expand(Node node);
    List<Pair<S, A>> rollout(S state);
    void backup(Node node, List<Pair<S, A>> playout);
}
