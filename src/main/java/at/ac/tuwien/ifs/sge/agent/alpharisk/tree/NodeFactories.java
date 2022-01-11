package at.ac.tuwien.ifs.sge.agent.alpharisk.tree;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.decision.*;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public class NodeFactories {

    public static Node makeNode(Node parent, RiskState state, RiskAction action) {
        switch (state.getPhase()) {
            case ATTACK:
                return new AttackNode(parent, state, action);
            case TERMINATED:
                return new TerminalNode(parent, state, action);
            default:
                return new DefaultDecisionNode(parent, state, action);
        }
    }

    public static Node makeRoot(RiskState state) {
        return makeNode(null, state, null);
    }
}
