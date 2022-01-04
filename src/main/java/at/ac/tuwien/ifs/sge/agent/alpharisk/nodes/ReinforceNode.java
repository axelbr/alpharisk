package at.ac.tuwien.ifs.sge.agent.alpharisk.nodes;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReinforceNode extends AbstractNode {

    private final Set<RiskAction> actions;
    private Function<List<Integer>, List<Integer>> denomination;
    private Set<Integer> blockingTerritories = new HashSet<>();

    public ReinforceNode(RiskState state, RiskAction previousAction) {
       this(state, previousAction, (List<Integer> a) -> List.of(a.stream().max(Integer::compare).get()), new HashSet<>());
    }

    public ReinforceNode(RiskState state, RiskAction previousAction, Function<List<Integer>, List<Integer>> denomination, Set<Integer> blockingTerritories) {
        super(state, previousAction);
        this.denomination = denomination;
        if (blockingTerritories != null) {
            this.blockingTerritories.addAll(blockingTerritories);
        }
        this.actions = computePossibleActions(state);
    }

    @Override
    public Set<RiskAction> getPossibleActions() {
        return actions;
    }

    private Set<RiskAction> computePossibleActions(RiskState state) {
        var board = state.getBoard();
        Map<Integer, List<Integer>> reinforcements = new HashMap<>();
        var frontReinforcements = super.getPossibleActions().stream()
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
        return actions;
    }
}
