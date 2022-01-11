package at.ac.tuwien.ifs.sge.agent.alpharisk.tree.decision;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public class DefaultDecisionNode extends DecisionNode {

    public DefaultDecisionNode(Node parent, RiskState state, RiskAction previousAction) {
        super(parent, state, previousAction);
    }

    @Override
    protected Node createChild(RiskState state, RiskAction action) {
        var nextState = state.apply(action);
        return new DefaultDecisionNode(this, nextState, action);
    }
}
