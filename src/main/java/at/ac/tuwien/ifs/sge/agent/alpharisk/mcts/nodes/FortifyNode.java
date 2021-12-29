package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.nodes;

import at.ac.tuwien.ifs.sge.agent.alpharisk.Phase;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public class FortifyNode extends AbstractNode {

    public FortifyNode(Risk game) {
        super(game, Phase.FORTIFY);
    }

    @Override
    public double computeHeuristic(RiskAction action) {
        return 0;
    }
}
