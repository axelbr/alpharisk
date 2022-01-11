package at.ac.tuwien.ifs.sge.agent.alpharisk.tree.decision;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.AbstractNode;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.HashSet;
import java.util.Set;

public class TerminalNode extends AbstractNode {

    public TerminalNode(Node parent, RiskState state, RiskAction action) {
        super(parent, state, action);
    }

    @Override
    public double getValue() {
        return getState().hasWon() ? 1.0 : 0.0;
    }

    @Override
    public Set<RiskAction> expandedActions() {
        return new HashSet<>();
    }

    @Override
    public Node select(RiskAction action) {
        return null;
    }

    @Override
    public Node expand(RiskAction action) {
        return null;
    }

    @Override
    public boolean isFullyExpanded() {
        return true;
    }
}
