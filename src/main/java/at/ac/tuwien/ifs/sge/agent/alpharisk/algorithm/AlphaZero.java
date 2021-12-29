package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm;

import ai.djl.ndarray.types.Shape;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.tree.Tree;

import java.util.ArrayList;

public class AlphaZero {

    public class Config {
        int numberOfSimulations = 10;
    }

    private ActionValueModel model;
    private final int playerId;
    private final Config config;

    public AlphaZero(int playerId, ActionValueModel model) {
        this.model = model;
        this.config = new Config();
        this.playerId = playerId;
    }

    public TreeNode run(final Risk state, int numSimulations) {
        TreeNode root = TreeNode.makeRoot(state);
        if (this.model == null){
            this.model = new ActionValueModel(new Shape(5),new ArrayList<>());
        }
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
