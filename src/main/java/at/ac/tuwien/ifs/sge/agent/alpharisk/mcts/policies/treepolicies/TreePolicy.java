package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.treepolicies;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.Policy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

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
