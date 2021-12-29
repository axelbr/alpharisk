package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.nodes;

import at.ac.tuwien.ifs.sge.agent.alpharisk.Phase;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public class DefaultNode extends AbstractNode {
    @Override
    public double computeHeuristic(RiskAction action) {
        return 0;
    }

    public DefaultNode(Risk game, Phase phase) {
        super(game, phase);
    }
}
