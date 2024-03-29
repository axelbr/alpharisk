package at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.RiskState;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.*;

public abstract class DecisionNode extends AbstractNode {

    private final HashMap<RiskAction, Node> expandedChildren;
    private final Set<RiskAction> untriedActions;

    public DecisionNode(Node parent, final RiskState state, RiskAction previousAction) {
        super(parent, state, previousAction);
        expandedChildren = new HashMap<>();
        untriedActions = new HashSet<>(state.getPossibleActions());
    }

    protected abstract Node createChild(RiskState state, RiskAction action);

    @Override
    public Set<RiskAction> expandedActions() {
        return expandedChildren.keySet();
    }

    @Override
    public Node select(RiskAction action) {
        return expandedChildren.getOrDefault(action, null);
    }

    @Override
    public Node expand(RiskAction action) {
        if (!isFullyExpanded() && untriedActions.contains(action)) {
            var child = createChild(getState(), action);
            addChild(child);
            return child;
        } else {
            return null;
        }
    }

    @Override
    public void addChild(Node node) {
        if (!expandedChildren.containsKey(node.getAction()) && untriedActions.contains(node.getAction())) {
            untriedActions.remove(node.getAction());
            expandedChildren.put(node.getAction(), node);
        }
    }

    @Override
    public Collection<? extends Node> expandedChildren() {
        return expandedChildren.values();
    }

    @Override
    public boolean isFullyExpanded() {
        return untriedActions.isEmpty();
    }

}
