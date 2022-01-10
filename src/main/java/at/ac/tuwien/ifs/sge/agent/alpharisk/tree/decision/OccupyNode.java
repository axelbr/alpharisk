package at.ac.tuwien.ifs.sge.agent.alpharisk.tree.decision;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.selection.TreePolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.Set;
import java.util.stream.Collectors;

public class OccupyNode extends DecisionNode {
    public OccupyNode(Node parent, RiskState state, RiskAction previousAction, TreePolicy treePolicy) {
        super(parent, state, previousAction, treePolicy);
    }

    @Override
    public Set<RiskAction> getPossibleActions() {
        var possibleActions = getState().getGame().getPossibleActions();
        int maxTroops = possibleActions.stream()
                .map(RiskAction::troops)
                .max(Integer::compare)
                .orElseThrow();
        return possibleActions.stream()
                .filter(a -> a.troops() == 3 || a.troops() == maxTroops || a.troops() == 1)
                .collect(Collectors.toSet());
    }

}
