package at.ac.tuwien.ifs.sge.agent.alpharisk.tree;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.TreePolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.RolloutPolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.SimulationStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.chance.ChanceNode;
import at.ac.tuwien.ifs.sge.util.Util;
import com.google.common.collect.Sets;

import java.util.function.Function;
import java.util.stream.Collectors;

public class SearchTree {

    private TreePolicy treePolicy;
    private Node root;
    private RolloutPolicy rolloutPolicy;
    private SimulationStrategy simulationStrategy;

    public static Function<Node, SearchTree> getSearchTreeBuilder(RolloutPolicy rolloutPolicy, TreePolicy treePolicy, SimulationStrategy simulationStrategy) {
        return node -> new SearchTree(node, rolloutPolicy, treePolicy, simulationStrategy);
    }

    public SearchTree(Node root, RolloutPolicy rolloutPolicy, TreePolicy treePolicy, SimulationStrategy simulationStrategy) {
        this.root = root;
        this.rolloutPolicy = rolloutPolicy;
        this.treePolicy = treePolicy;
        this.simulationStrategy = simulationStrategy;
    }

    public SearchTree(Node root, SearchTree other) {
        this(root, other.rolloutPolicy, other.treePolicy, other.simulationStrategy);
    }

    public Node getRoot() {
        return root;
    }

    public Node select() {
        var current = root;
        while (current != null && current.isFullyExpanded() && current.getState().getPhase() != RiskState.Phase.TERMINATED) {
            var action = treePolicy.selectAction(current);
            if (action == null) {
                break;
            }
            current = current.select(action);
        }
        return current;
    }

    public void rollout(Node node) {
        double value = simulationStrategy.simulate(node, rolloutPolicy);
        node.update(value);
    }

    public Node expand(Node node) {
        if (node != null && !node.isFullyExpanded()) {
            var actions = node.getState().getPossibleActions();
            var remaining = Sets.difference(actions, node.expandedActions());
            var action = Util.selectRandom(remaining);
            var child = node.expand(action);
            return child;
        } else {
            return null;
        }
    }

    public SearchTree findRoot(RiskState newState) {
        Node newRoot = NodeFactories.makeRoot(newState);
        if (root == null || true) {
            return new SearchTree(newRoot, this);
        }
        var actions = newState.getGame().getActionRecords().stream()
                .skip(root.getState().getGame().getNumberOfActions())
                .collect(Collectors.toList());
        var current = root;
        for (var action: actions) {
            var selection = current.select(action.getAction());
            if (selection != null) {
                current = selection;
            } else {
                current = null;
                break;
            }
        }
        if (current != null) {
            newRoot = current;
        }
        return new SearchTree(newRoot, this);
    }

    public int size() {
        return root.size();
    }
}
