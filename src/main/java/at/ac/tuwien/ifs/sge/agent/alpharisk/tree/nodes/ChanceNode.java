package at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.factories.NodeFactory;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.*;

public abstract class ChanceNode extends AbstractNode {


    public ChanceNode(Node parent, RiskState state, RiskAction action) {
        super(parent, parent.getState(), action);
    }

    public abstract double getOutcomeProbability(Node child);

    @Override
    public final Node expand(RiskAction action) {
        throw new IllegalStateException("Chance nodes cannot be expanded.");
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
