package at.ac.tuwien.ifs.sge.agent.alpharisk.domain;

import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OccupyState extends RiskState{
    private Set<RiskAction> possibleActions;

    public OccupyState(Risk risk, Phase phase) {
        super(risk, phase);
        possibleActions = computePossibleActions(risk);
    }

    private Set<RiskAction> computePossibleActions(Risk game) {
        var possibleActions = game.getPossibleActions();
        int maxTroops = possibleActions.stream()
                .map(RiskAction::troops)
                .max(Integer::compare)
                .orElseThrow();
        return possibleActions.stream()
                .filter(a -> a.troops() == 3 || a.troops() == maxTroops || a.troops() == 1)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<RiskAction> getPossibleActions() {
        return possibleActions;
    }
}
