package at.ac.tuwien.ifs.sge.agent.alpharisk.nodes;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public class NodeFactory {
    private NodeFactory() {}

    public static Node makeNode(RiskState state, RiskAction action) {
        switch (state.getPhase()) {
            case INITIAL_SELECT:
            case INITIAL_REINFORCE:
            case TRADE_IN:
                return new DefaultNode(state, action);
            case REINFORCE:
                return new ReinforceNode(state, action);
            case FORTIFY:
                return new FortifyNode(state, action);
            case OCCUPY:
                return new OccupyNode(state, action);
            case ATTACK:
                return new AttackNode(state, action);
            case TERMINATED:
                return new TerminalNode(state, action);
            default:
                throw new IllegalArgumentException(state.getPhase().toString());
        }
    }

    public static Node makeRoot(RiskState state) {
        return makeNode(state, null);
    }

}
