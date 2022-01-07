package at.ac.tuwien.ifs.sge.agent.alpharisk.tree.decision;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.selection.TreePolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.Set;

public class DefaultNode extends DecisionNode {
    public DefaultNode(Node parent, RiskState state, RiskAction previousAction, TreePolicy treePolicy) {
        super(parent, state, previousAction, treePolicy);
    }

    @Override
    public Set<RiskAction> getPossibleActions() {
        return getState().getGame().getPossibleActions();
    }
}
