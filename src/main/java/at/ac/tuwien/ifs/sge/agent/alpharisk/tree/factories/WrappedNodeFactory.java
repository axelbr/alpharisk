package at.ac.tuwien.ifs.sge.agent.alpharisk.tree.factories;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.NodeWrapper;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.function.Function;

public class WrappedNodeFactory extends NodeFactory {

    private final Function<Node, Node> wrapperFunction;
    private final NodeFactory factory;

    public WrappedNodeFactory(NodeFactory factory, Function<Node, Node> wrapperFunction) {
        this.wrapperFunction = wrapperFunction;
        this.factory = factory;
    }

    @Override
    public Node makeNode(Node parent, RiskState state, RiskAction action) {
        var node = factory.makeNode(parent, state, action);
        return wrapperFunction.apply(node);
    }
}
