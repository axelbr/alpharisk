package at.ac.tuwien.ifs.sge.agent.alpharisk.tree.factories;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.Node;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public abstract class NodeFactory {

    private static NodeFactory instance;
    public static void setInstance(NodeFactory factory) {
        instance = factory;
    }

    public static Node node(Node parent, RiskState state, RiskAction action) {
        return instance.makeNode(parent, state, action);
    }

    public static Node root(RiskState state) {
        return instance.makeRoot(state);
    }

    public abstract Node makeNode(Node parent, RiskState state, RiskAction action);
    public final Node makeRoot(RiskState state) {
        return makeNode(null, state, null);
    }
}
