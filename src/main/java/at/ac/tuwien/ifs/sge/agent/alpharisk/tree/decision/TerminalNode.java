package at.ac.tuwien.ifs.sge.agent.alpharisk.tree.decision;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.selection.TreePolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.HashSet;
import java.util.Set;

public class TerminalNode extends DecisionNode {

    public TerminalNode(Node parent, RiskState state, RiskAction previousAction, TreePolicy treePolicy) {
        super(parent, state, previousAction, treePolicy);
    }

    @Override
    public Set<RiskAction> getPossibleActions() {
        return new HashSet<>();
    }

    @Override
    public void addChild(Node node) {

    }



    @Override
    public int size() {
        return 1;
    }

    @Override
    public Node expand() {
        return null;
    }

    @Override
    public double getValue() {
        var board = this.getState().getBoard();
        return getState().hasWon() ? 1.0 : 0.0;
    }

    @Override
    public String toString() {
        return String.format("Terminal(%s)", getState().hasWon() ? "win" : "loss");
    }
}
