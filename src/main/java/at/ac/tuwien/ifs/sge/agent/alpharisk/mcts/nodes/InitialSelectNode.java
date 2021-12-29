package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.nodes;

import at.ac.tuwien.ifs.sge.agent.alpharisk.Phase;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public class InitialSelectNode extends AbstractNode {

    public InitialSelectNode(Risk game) {
        super(game, Phase.INITIAL_SELECT);
    }

    @Override
    public double computeHeuristic(RiskAction action) {
        return 0;
    }
}
