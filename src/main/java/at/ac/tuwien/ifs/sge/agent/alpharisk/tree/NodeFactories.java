package at.ac.tuwien.ifs.sge.agent.alpharisk.tree;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.selection.TreePolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.decision.*;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public class NodeFactories {

    private static TreePolicy treePolicy;

    public static void setTreePolicy(TreePolicy treePolicy) {
        NodeFactories.treePolicy = treePolicy;
    }

    public interface NodeFactory {
        Node makeNode(Node parent, RiskState state, RiskAction action);
        default Node makeRoot(RiskState state) {
            return makeNode(null, state, null);
        }
    }

    public static class DecisionNodeFactory implements NodeFactory {
        private TreePolicy treePolicy;

        public DecisionNodeFactory(TreePolicy treePolicy) {
            this.treePolicy = treePolicy;
        }

        @Override
        public Node makeNode(Node parent, RiskState state, RiskAction action) {
            switch (state.getPhase()) {
                case INITIAL_SELECT:
                case INITIAL_REINFORCE:
                case TRADE_IN:
                    return new DefaultNode(parent, state, action, treePolicy);
                case REINFORCE:
                    return new ReinforceNode(parent, state, action, treePolicy);
                case FORTIFY:
                    return new FortifyNode(parent, state, action, treePolicy);
                case OCCUPY:
                    return new OccupyNode(parent, state, action, treePolicy);
                case ATTACK:
                    return new AttackNode(parent, state, action, treePolicy);
                case TERMINATED:
                    return new TerminalNode(parent, state, action, treePolicy);
                default:
                    throw new IllegalArgumentException(state.getPhase().toString());
            }
        }
    }

    public static NodeFactory decisionNodeFactory(TreePolicy treePolicy) {
        return new DecisionNodeFactory(treePolicy);
    }

    public static NodeFactory decisionNodeFactory() {
        return new DecisionNodeFactory(treePolicy);
    }

}
