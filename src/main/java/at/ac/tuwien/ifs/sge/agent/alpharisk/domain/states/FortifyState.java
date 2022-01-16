package at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states;

import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.*;
import java.util.stream.Collectors;

public class FortifyState extends RiskState {
    private final int IDLE_TROOPS_SIZE = 10;
    private final Set<RiskAction> actions;

    public FortifyState(Risk risk, Phase phase) {
        super(risk, phase);
        this.actions = computeActions(risk);
    }

    public Set<RiskAction> computeActions(Risk state) {
        var board = state.getBoard();
        var possibleActions = state.getPossibleActions();
        Map<Integer, Map<Integer, List<Integer>>> fortifications = new HashMap<>();
        var frontFortifications = possibleActions.stream()
                .filter(a -> a.fortifyingId() >= 0 && (board.neighboringEnemyTerritories(a.fortifiedId()).size() > 0 || a.troops()>=IDLE_TROOPS_SIZE))
                .collect(Collectors.toSet());
        for (var action: frontFortifications) {
            if (!fortifications.containsKey(action.fortifyingId())) {
                fortifications.put(action.fortifyingId(), new HashMap<>());
            }
            if (!fortifications.get(action.fortifyingId()).containsKey(action.fortifiedId())) {
                fortifications.get(action.fortifyingId()).put(action.fortifiedId(), new ArrayList<>());
            }
            fortifications.get(action.fortifyingId()).get(action.fortifiedId()).add(action.troops());
        }
        Set<RiskAction> actions = new HashSet<>();
        actions.add(RiskAction.endPhase());
        for (var src: fortifications.keySet()) {
            for (var target: fortifications.get(src).keySet()) {

                var troops = fortifications.get(src).get(target);
                int max = troops.stream().max(Integer::compare).get();
                actions.add(RiskAction.fortify(src, target, max));
                if (troops.size() > 2) {
                    int half = Integer.divideUnsigned(max, 2);
                    actions.add(RiskAction.fortify(src, target, half));
                }
            }
        }
        return actions;
    }

    @Override
    public Set<RiskAction> getPossibleActions() {
        return actions;
    }
}
