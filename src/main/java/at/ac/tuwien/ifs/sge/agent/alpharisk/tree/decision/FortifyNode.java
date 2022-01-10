package at.ac.tuwien.ifs.sge.agent.alpharisk.tree.decision;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.selection.TreePolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.*;
import java.util.stream.Collectors;

public class FortifyNode extends DecisionNode {
    private final Set<RiskAction> actions;

    public FortifyNode(Node parent, RiskState state, RiskAction previousAction, TreePolicy treePolicy) {
        super(parent, state, previousAction, treePolicy);
        this.actions = computeActions();
    }

    @Override
    public Set<RiskAction> getPossibleActions() {
        return actions;
    }

    public Set<RiskAction> computeActions() {
        var board = getState().getBoard();
        var possibleActions = getState().getGame().getPossibleActions();
        Map<Integer, Map<Integer, List<Integer>>> fortifications = new HashMap<>();
        var frontFortifications = possibleActions.stream()
                .filter(a -> a.fortifyingId() >= 0 && board.neighboringEnemyTerritories(a.fortifiedId()).size() > 0)
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


}
