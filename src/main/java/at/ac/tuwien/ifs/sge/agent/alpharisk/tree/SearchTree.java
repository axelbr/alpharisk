package at.ac.tuwien.ifs.sge.agent.alpharisk.tree;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.decision.DecisionNode;

public class SearchTree {

    private Node root;

    public SearchTree(Node root) {
        this.root = root;
    }

    public SearchTree reRoot(RiskState state) {
        return null;
    }

    public Node getRoot() {
        return root;
    }


}
