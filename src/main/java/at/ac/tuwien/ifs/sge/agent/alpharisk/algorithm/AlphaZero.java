package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm;

import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.tree.Tree;

public class AlphaZero {

    public class Config {
        int numberOfSimulations = 10;
    }

    private final ActionValueModel model;
    private final int playerId;
    private final Config config;

    public AlphaZero(int playerId, ActionValueModel model) {
        this.model = model;
        this.config = new Config();
        this.playerId = playerId;
    }

    public TreeNode run(final Risk state, int numSimulations) {
        TreeNode root = TreeNode.makeRoot(state);
        root.expand(model);
        for (int i = 0; i < numSimulations; i++) {
            TreeNode current = root;
            current = current.select();
            current.expand(model);
            current.backpropagate(this.playerId);
        }
        return root;
    }

    public RiskAction computeAction(final Risk state, int numSimulations) {
        TreeNode root = run(state, numSimulations);
        return root.selectChild().getAction();
    }
}
