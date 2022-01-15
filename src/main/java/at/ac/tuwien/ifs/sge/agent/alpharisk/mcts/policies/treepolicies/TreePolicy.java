package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.treepolicies;

import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.Policy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.Node;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.Comparator;

public abstract class TreePolicy implements Policy<Node, RiskAction> {

    @Override
    public RiskAction selectAction(Node node) {
        if (node.expandedChildren().isEmpty()) {
            throw new IllegalStateException("Node must have expanded children.");
        }
        var action = node.expandedChildren().stream()
                .max(Comparator.comparing(this::computeScore))
                .map(Node::getAction)
                .orElseThrow();
        return action;
    }

    public abstract double computeScore(Node node);
}
