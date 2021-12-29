package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.nodes;

import at.ac.tuwien.ifs.sge.agent.alpharisk.Phase;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;

public class NodeFactory {
    private NodeFactory() {}

    public static Node makeNode(Risk game, Phase phase) {
        switch (phase) {
            case INITIAL_SELECT:
                return new InitialSelectNode(game);
            case INITIAL_REINFORCE:
                return new InitialReinforceNode(game);
            case REINFORCE:
                return new ReinforceNode(game);
            case ATTACK:
                return new AttackNode(game);
            case OCCUPY:
                return new OccupyNode(game);
            case FORTIFY:
                return new FortifyNode(game);
            default:
                return new DefaultNode(game, phase);
        }
    }
}
