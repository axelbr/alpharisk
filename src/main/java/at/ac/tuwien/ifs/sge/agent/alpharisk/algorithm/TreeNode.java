package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm;

import ai.djl.ndarray.NDArray;
import at.ac.tuwien.ifs.sge.agent.alpharisk.util.MemoryManager;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.*;

public class TreeNode {

    public static class Metrics {
        NDArray value, prior;
        int visitCount;

        public Metrics(NDArray value, NDArray prior, int visitCount) {
            this.value = value;
            this.prior = prior;
            this.visitCount = visitCount;
        }
    }

    private final Risk state;
    private final RiskAction action;
    private final List<TreeNode> children = new ArrayList<>();
    private TreeNode parent;
    private Metrics metrics;

    public static TreeNode makeRoot(Risk state) {
        return new TreeNode(state, null, null, null);
    }

    TreeNode(Risk state, RiskAction action, TreeNode parent, Metrics metrics) {
        this.state = state;
        this.parent = parent;
        this.metrics = metrics;
        this.action = action;
    }

    public RiskAction getAction() {
        return action;
    }

    public double computeUCTScore(double c1) {
        double priorScore = metrics.prior.getDouble() * Math.sqrt((double) parent.metrics.visitCount / (metrics.visitCount + 1));
        double qValues = metrics.value.getDouble() / (metrics.visitCount + 1);
        return qValues + c1 * priorScore;
    }

    public void expand(ActionValueModel model) {
        assert isLeaf();
        for (RiskAction action: state.getPossibleActions()) {
            NDArray probs = model.getActionProbability(action, state);
            Risk nextState = (Risk) state.doAction(action);
            nextState = skipNoPlayerActions(nextState);
            NDArray value;
            if (nextState.isGameOver()) {
                 value = MemoryManager.getManager().create(nextState.getUtilityValue(nextState.getCurrentPlayer()));
            } else {
                value = model.getValue(nextState);
            }
            TreeNode child = new TreeNode(nextState, action, this, new Metrics(value, probs, 0));
            children.add(child);
        }
    }

    public TreeNode selectChild() {
        return children.stream()
                .max(Comparator.comparingDouble(a -> a.computeUCTScore(1.0)))
                .orElseThrow();
    }

    public TreeNode select() {
        TreeNode current = this;
        while (!current.isLeaf()) {
            current = current.selectChild();
        }
        return current;
    }

    public void backpropagate(int playerId) {
        TreeNode current = this.parent;
        while (current != null) {
            if (playerId == state.getCurrentPlayer()) {
                current.metrics.value.add(this.metrics.value);
            } else  {
                current.metrics.value.sub(this.metrics.value);
            }
            current.metrics.visitCount++;
            current = current.parent;
        }
    }

    private boolean isLeaf() {
        return children.isEmpty();
    }

    private Risk skipNoPlayerActions(final Risk state) {
        Risk current = state;
        while (current.getCurrentPlayer() < 0) {
            current = (Risk) current.doAction();
        }
        return current;
    }
}
