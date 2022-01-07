package at.ac.tuwien.ifs.sge.agent.alpharisk.tree;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.Collection;
import java.util.Optional;

public interface Node {
    Optional<RiskAction> getAction();
    RiskState getState();
    Node getParent();
    Collection<? extends Node> getChildren();
    void addChild(Node node);
    double getValue();
    int getVisits();

    int size();


    Node select();
    Node expand();
    void update(double value);

    default boolean isLeaf() {
        return getChildren().isEmpty();
    }
    boolean isFullyExpanded();
    default boolean isRoot() {
        return getParent() == null;
    }
}
