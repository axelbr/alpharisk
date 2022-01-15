package at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states;

import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.Set;

public class DefaultState extends RiskState {

    private Set<RiskAction> actions;
    public DefaultState(Risk risk, Phase phase) {
        super(risk, phase);
        actions = risk.getPossibleActions();
    }

    @Override
    public Set<RiskAction> getPossibleActions() {
        return actions;
    }
}
