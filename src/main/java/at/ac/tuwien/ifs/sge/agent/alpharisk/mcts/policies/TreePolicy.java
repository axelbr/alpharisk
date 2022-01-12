package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.Policy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.stream.Collectors;

public abstract class TreePolicy implements Policy<Node, RiskAction> {
    @Override
    public RiskAction selectAction(Node node) {
        double bestScore = -Double.MAX_VALUE;
        RiskAction bestAction = null;
        var nonTerminalNodes = node.expandedChildren().stream()
                .filter(c -> c != null && c.getState().getPhase() != RiskState.Phase.TERMINATED)
                .collect(Collectors.toList());
        for (var child : nonTerminalNodes) {
            double score = computeScore(child);
            if (score > bestScore) {
                bestScore = score;
                bestAction = child.getAction();
            }
        }
        return bestAction;
    }

    public abstract double computeScore(Node node);
}
