package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.nodes;

import at.ac.tuwien.ifs.sge.agent.alpharisk.Phase;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public class InitialReinforceNode extends AbstractNode {

    public InitialReinforceNode(Risk game) {
        super(game, Phase.INITIAL_REINFORCE);
    }

    @Override
    public double computeHeuristic(RiskAction action) {
        return 0;
    }
}
