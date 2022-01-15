package at.ac.tuwien.ifs.sge.agent.alpharisk.domain.nodes;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.factories.NodeFactory;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.DecisionNode;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public class DefaultDecisionNode extends DecisionNode {

    public DefaultDecisionNode(Node parent, RiskState state, RiskAction previousAction) {
        super(parent, state, previousAction);
    }

    @Override
    protected Node createChild(RiskState state, RiskAction action) {
        var nextState = state.apply(action);
        return NodeFactory.node(this, nextState, action);
    }

}
