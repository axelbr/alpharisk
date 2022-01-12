package at.ac.tuwien.ifs.sge.agent.alpharisk.domain;

import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import com.google.common.collect.ImmutableSet;

import java.util.Set;
import java.util.stream.Collectors;

public class AttackState extends RiskState {

    private final Set<RiskAction> possibleActions;

    public AttackState(Risk risk, Phase phase) {
        super(risk, phase);
        possibleActions = computeActions(risk);
    }

    private Set<RiskAction> computeActions(Risk risk) {
        var possibleActions = risk.getPossibleActions();
        var board = risk.getBoard();
        var actions = possibleActions.stream()
                .filter(a -> (a.troops() == board.getMaxAttackingTroops(a.attackingId()))
                        || a.isEndPhase() || a.isBonus())
                .filter(a -> a.isEndPhase() || a.isBonus() || board.getTerritoryTroops(a.attackingId()) > board.getTerritoryTroops(a.defendingId()))
                .collect(Collectors.toSet());
        if (actions.isEmpty()) {
            return possibleActions;
        } else {
            return actions;
        }
    }

    @Override
    public Set<RiskAction> getPossibleActions() {
        return possibleActions;
    }
}
