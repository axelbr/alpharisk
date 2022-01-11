package at.ac.tuwien.ifs.sge.agent.alpharisk.domain;

import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InitialReinforceState extends RiskState{
    private Set<RiskAction> possibleActions;
    private Function<List<Integer>, List<Integer>> denomination;
    private Set<Integer> blockingTerritories;

    public InitialReinforceState(Risk risk, Phase phase, Function<List<Integer>, List<Integer>> denomination, Set<Integer> blockingTerritories) {
        super(risk, phase);
        this.denomination = denomination;
        this.blockingTerritories = blockingTerritories;
        possibleActions = computePossibleActions(risk);
    }

    public InitialReinforceState(Risk risk, Phase phase) {
        this(risk, phase, (List<Integer> a) -> List.of(a.stream().max(Integer::compare).get()), new HashSet<>());
    }

    private Set<RiskAction> computePossibleActions(Risk state) {
        var board = state.getBoard();
        return state.getPossibleActions().stream()
                .filter(a -> a.reinforcedId() < 0 || !board.neighboringEnemyTerritories(a.reinforcedId()).isEmpty())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<RiskAction> getPossibleActions() {
        return possibleActions;
    }
}
