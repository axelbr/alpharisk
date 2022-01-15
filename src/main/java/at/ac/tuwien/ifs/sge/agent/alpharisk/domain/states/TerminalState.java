package at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states;

import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.HashSet;
import java.util.Set;

public class TerminalState extends RiskState {

    public TerminalState(Risk risk, Phase phase) {
        super(risk, phase);
    }

    @Override
    public Set<RiskAction> getPossibleActions() {
        return new HashSet<>();
    }


}
