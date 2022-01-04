package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.expansion;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.nodes.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.nodes.NodeFactory;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.Util;
import at.ac.tuwien.ifs.sge.util.tree.Tree;

public class ExpandAllSelectRandom implements ExpansionStrategy {
    @Override
    public Tree<Node> apply(Tree<Node> tree) {
        assert tree.isLeaf();
        Node node = tree.getNode();
        var possibleActions = node.getPossibleActions();
        assert !possibleActions.isEmpty();
        for (RiskAction action: possibleActions) {
            RiskState nextState = node.getState().apply(action);
            Node next = NodeFactory.makeNode(nextState, action);
            tree.add(next);
        }
        return Util.selectRandom(tree.getChildren());
    }
}