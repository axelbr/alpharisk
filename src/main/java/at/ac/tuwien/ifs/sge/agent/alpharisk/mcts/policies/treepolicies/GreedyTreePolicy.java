package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.treepolicies;

import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.Node;

public class GreedyTreePolicy extends TreePolicy {
    @Override
    public double computeScore(Node node) {
        return node.getValue();
    }
}
