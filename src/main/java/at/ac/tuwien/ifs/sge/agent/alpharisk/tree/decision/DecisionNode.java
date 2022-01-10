package at.ac.tuwien.ifs.sge.agent.alpharisk.tree.decision;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.selection.TreePolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.AbstractNode;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.NodeFactories;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.Util;

import java.util.*;

public abstract class DecisionNode extends AbstractNode {

    private final TreePolicy treePolicy;
    private final NodeFactories.NodeFactory nodeFactory;
    private final Set<RiskAction> untriedActions;

    public DecisionNode(Node parent, final RiskState state, RiskAction previousAction, TreePolicy treePolicy) {
        super(parent, state, previousAction);
        this.treePolicy = treePolicy;
        untriedActions = state.getGame().getPossibleActions();
        nodeFactory = NodeFactories.decisionNodeFactory(treePolicy);
    }

    public abstract Set<RiskAction> getPossibleActions();

    public NodeFactories.NodeFactory getNodeFactory() {
        return nodeFactory;
    }

    @Override
    public Node expand() {
        var action = Util.selectRandom(untriedActions);
        untriedActions.remove(action);
        var nextState = getState().apply(action);
        var node = nodeFactory.makeNode(this, nextState, action);
        addChild(node);
        return node;
    }

    @Override
    public Node select() {
        return treePolicy.apply(getChildren());
    }

    @Override
    public Optional<? extends Node> select(RiskAction action) {
        return getChildren().stream().filter(c -> c.getAction().get().equals(action))
                .findAny();
    }

    @Override
    public boolean isFullyExpanded() {
        return untriedActions.isEmpty();
    }
}
