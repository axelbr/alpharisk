package at.ac.tuwien.ifs.sge.agent.alpharisk.nodes;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.Set;
import java.util.stream.Collectors;

public class OccupyNode extends AbstractNode {
    public OccupyNode(RiskState state, RiskAction previousAction) {
        super(state, previousAction);
    }

    @Override
    public Set<RiskAction> getPossibleActions() {
        int maxTroops = super.getPossibleActions().stream()
                .map(RiskAction::troops)
                .max(Integer::compare)
                .orElseThrow();
        return super.getPossibleActions().stream()
                .filter(a -> a.troops() == 3 || a.troops() == maxTroops || a.troops() == 1)
                .collect(Collectors.toSet());
    }
}
