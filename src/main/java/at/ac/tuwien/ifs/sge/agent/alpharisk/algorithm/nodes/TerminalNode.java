package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes;

import at.ac.tuwien.ifs.sge.agent.alpharisk.Phase;
import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public class TerminalNode extends AbstractNode {

    public TerminalNode(RiskState state, RiskAction previousAction) {
        super(state, previousAction);
        assert state.getPhase() == Phase.TERMINATED;
    }

    @Override
    public double getValue() {
        var board = this.getState().getBoard();
        return board.getNrOfTerritoriesOccupiedByPlayer(getState().getCurrentPlayer()) > 0 ? 1.0 : -1.0;
    }

}
