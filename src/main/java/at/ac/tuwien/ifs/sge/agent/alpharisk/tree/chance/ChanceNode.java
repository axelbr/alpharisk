package at.ac.tuwien.ifs.sge.agent.alpharisk.tree.chance;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.AbstractNode;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.*;

public abstract class ChanceNode extends AbstractNode {


    public ChanceNode(Node parent, RiskState state, RiskAction action) {
        super(parent, state, action);
    }

    public abstract double getOutcomeProbability(Node child);

    @Override
    public Node expand(RiskAction action) {
        return select(null);
    }

    @Override
    public final Set<RiskAction> expandedActions() {
        return Set.of(getAction());
    }

    @Override
    public final boolean isLeaf() {
        return false;
    }

    @Override
    public final boolean isFullyExpanded() {
        return true;
    }

    @Override
    public String toString() {
        return "Chance" + super.toString();
    }
}
