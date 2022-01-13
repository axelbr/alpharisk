package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.expansion;

import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.Util;
import com.google.common.collect.Sets;

public class ExpandRandomAction implements ExpansionStrategy {
    @Override
    public RiskAction expand(Node node) {
        var actions = node.getState().getPossibleActions();
        var remaining = Sets.difference(actions, node.expandedActions());
        return Util.selectRandom(remaining);
    }
}
