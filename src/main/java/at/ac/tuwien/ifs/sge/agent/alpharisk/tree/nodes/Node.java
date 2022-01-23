package at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.factories.NodeFactory;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.Collection;
import java.util.Set;

public interface Node {
    RiskAction getAction();
    RiskState getState();
    Node getParent();
    double getValue();
    int getVisits();

    Collection<? extends Node> expandedChildren();
    Set<RiskAction> expandedActions();

    int size();

    Node select(RiskAction action);
    Node expand(RiskAction action);
    void update(NodeStatistics statistics);
    NodeStatistics getStatistics();
    void addChild(Node node);

    default boolean isLeaf() {
        return expandedChildren().isEmpty();
    }
    boolean isFullyExpanded();
    default boolean isRoot() {
        return getParent() == null;
    }
}
