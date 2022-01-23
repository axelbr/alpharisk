package at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.heuristics.ActionHeuristics;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.ActionValueFunction;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReinforceState extends RiskState{
    private Set<RiskAction> possibleActions;
    private Function<List<Integer>, List<Integer>> denomination;
    private Set<Integer> blockingTerritories;

    public ReinforceState(Risk risk, Phase phase, Function<List<Integer>, List<Integer>> denomination, Set<Integer> blockingTerritories) {
        super(risk, phase);
        this.denomination = denomination;
        this.blockingTerritories = blockingTerritories;
        possibleActions = computePossibleActions(risk);
        setUtilityFunction(ActionHeuristics.borderSecurityThreatHeuristic());
    }

    public ReinforceState(Risk risk, Phase phase) {
        this(risk, phase, (List<Integer> a) -> List.of(a.stream().max(Integer::compare).get()), new HashSet<>());
    }

    private Set<RiskAction> computePossibleActions(Risk state) {
        var board = state.getBoard();
        Map<Integer, List<Integer>> reinforcements = new HashMap<>();
        var possibleActions = state.getGame().getPossibleActions();
        var frontReinforcements = possibleActions.stream()
                .filter(a -> a.troops() > 0 && a.reinforcedId() >= 0 && (board.neighboringEnemyTerritories(a.reinforcedId()).size() > 0 || blockingTerritories.contains(a.reinforcedId())))
                .collect(Collectors.toSet());

        for (var action: frontReinforcements) {
            if (!reinforcements.containsKey(action.reinforcedId())) {
                reinforcements.put(action.reinforcedId(), new ArrayList<>());
            }
            reinforcements.get(action.reinforcedId()).add(action.troops());
        }
        Set<RiskAction> actions = new HashSet<>();
        for (var reinforceId: reinforcements.keySet()) {
            for (int troopCount: denomination.apply(reinforcements.get(reinforceId))) {
                actions.add(RiskAction.reinforce(reinforceId, troopCount));
            }
        }
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
