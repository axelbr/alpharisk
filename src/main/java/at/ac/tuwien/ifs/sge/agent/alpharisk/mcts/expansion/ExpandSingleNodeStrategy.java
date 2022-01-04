package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.expansion;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.nodes.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.nodes.NodeFactory;
import at.ac.tuwien.ifs.sge.util.Util;
import at.ac.tuwien.ifs.sge.util.tree.DoubleLinkedTree;
import at.ac.tuwien.ifs.sge.util.tree.Tree;
import com.google.common.collect.Sets;

import java.util.stream.Collectors;

public class ExpandSingleNodeStrategy implements ExpansionStrategy {
    @Override
    public Tree<Node> apply(Tree<Node> tree) {
        Node node = tree.getNode();
        var expandedActions = tree.getChildren().stream().map(c -> c.getNode().getAction()).collect(Collectors.toSet());
        var actionsLeft = Sets.difference(node.getState().getGame().getPossibleActions(), expandedActions);
        if (!actionsLeft.isEmpty()) {
            var action = Util.selectRandom(actionsLeft);
            RiskState nextState = node.getState().apply(action);
            Tree<Node> next = new DoubleLinkedTree<>(NodeFactory.makeNode(nextState, action));
            tree.add(next);
            return next;
        } else {
            throw new IllegalStateException("Tree already fully expanded");
        }
    }
}
