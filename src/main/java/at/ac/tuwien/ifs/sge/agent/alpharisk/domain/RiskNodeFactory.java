package at.ac.tuwien.ifs.sge.agent.alpharisk.domain;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.DecisionNode;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.nodes.AttackOutcomeNode;
import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.nodes.DefaultDecisionNode;
import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.nodes.TerminalNode;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.factories.NodeFactory;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public class RiskNodeFactory extends NodeFactory {
    @Override
    public Node makeNode(Node parent, RiskState state, RiskAction action) {
        if (parent != null && parent.getState().getPhase() == RiskState.Phase.ATTACK) {
            if (parent instanceof DecisionNode && isAttackAction(action)) {
                return new AttackOutcomeNode(parent, state, action);
            } else {
                return make(parent, state, action);
            }
        }
        return make(parent, state, action);
    }

    private Node make(Node parent, RiskState state, RiskAction action) {
        switch (state.getPhase()) {
            case TERMINATED:
                return new TerminalNode(parent, state, action);
            default:
                return new DefaultDecisionNode(parent, state, action);
        }
    }


    private boolean isAttackAction(RiskAction action) {
        return !action.isEndPhase() && !action.isCardIds() && !action.isBonus();
    }
}
