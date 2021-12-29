package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts;

import at.ac.tuwien.ifs.sge.agent.alpharisk.Phase;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.nodes.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.nodes.NodeFactory;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.util.MaxIterationsStoppingCriterion;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.Util;
import at.ac.tuwien.ifs.sge.util.pair.ImmutablePair;
import at.ac.tuwien.ifs.sge.util.tree.DoubleLinkedTree;
import at.ac.tuwien.ifs.sge.util.tree.Tree;
import com.google.common.collect.Streams;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.yaml.snakeyaml.events.SequenceStartEvent;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class MonteCarloTreeSearch  {
    private final Phase phase;
    private Tree<Node> searchTree;
    private RiskAction bestAction;

    public MonteCarloTreeSearch(Node root, Phase phase) {
        this.phase = phase;
        searchTree = new DoubleLinkedTree<>(root);
    }

    public void runSearch() {
        Tree<Node> tree = searchTree;
        while (true) {
            expand(tree);
            tree = select();
            double value = rollout(tree, new MaxIterationsStoppingCriterion(32));
            backpropagate(tree, value);
            bestAction = determineBestAction();
        }
    }

    public RiskAction getBestAction() {
        return bestAction;
    }

    private RiskAction determineBestAction() {
        return searchTree.getChildren().stream()
                .max(Comparator.comparingDouble(this::computeUpperConfidenceBound))
                .orElseThrow()
                .getNode().getGame().getActionRecords().stream().filter(r -> r.getPlayer() == searchTree.getNode().getGame().getCurrentPlayer())
                .collect(Collectors.toList()).get(0).getAction();
    }

    private Tree<Node> select() {
        Tree<Node> node = searchTree;
        while (!node.isLeaf()) {
            node = node.getChildren().stream()
                    .max(Comparator.comparingDouble(this::computeUpperConfidenceBound))
                    .orElseThrow();
        }
        return node;
    }

    private void expand(Tree<Node> leaf) {
        assert leaf.isLeaf();
        Node node = leaf.getNode();
        for (RiskAction action: node.getGame().getPossibleActions()) {
            Risk nextState = (Risk) node.getGame().doAction(action);
            Node next = NodeFactory.makeNode(nextState, phase.update(nextState));
            leaf.add(next);
        }
    }

    private void backpropagate(Tree<Node> tree, double value) {
        assert tree.isLeaf() && tree.getNode().getGame().isGameOver();
        Tree<Node> current = tree;
        while (current.getParent() != null) {
            current.getNode().update(value);
            current = current.getParent();
        }
    }

    private double rollout(Tree<Node> tree, StoppingCriterion criterion) {
        Node current = tree.getNode();
        while (!criterion.shouldStop() && !current.getGame().isGameOver()) {
            var state = (Risk) current.getGame();
            if (state.getCurrentPlayer() < 0) {
                state = (Risk) state.doAction();
            } else {
                var action = sampleAction(current, 30);
                state = (Risk) state.doAction(action);
            }
            Phase nextPhase = current.getPhase().update(state);
            current = NodeFactory.makeNode(state, nextPhase);
        }
        if (current.getGame().isGameOver()) {
            return current.getGame().getUtilityValue();
        } else {
            return current.computeHeuristic(selectBestActions(current, 1).get(0));
        }
    }

    private double computeUpperConfidenceBound(Tree<Node> tree) {
        Node node = tree.getNode();
        int n = Math.max(1, node.getPlays());
        int N = tree.getParent() != null ? tree.getParent().getNode().getPlays() : n;
        return (double) node.getWins() / node.getPlays() + Math.sqrt(Math.log(N) / n);
    }

    private List<RiskAction> selectBestActions(Node node, int kBest) {
        var actions = node.getGame().getPossibleActions();
        return actions.stream()
                .sorted(Comparator.comparingDouble(node::computeHeuristic))
                .limit(Math.min(kBest, actions.size()))
                .collect(Collectors.toList());
    }

    private RiskAction sampleAction(Node node, int kBest) {
        var actions = node.getGame().getPossibleActions();
        var actionScores = actions.stream()
                .map(action -> new Pair<>(action, Math.exp(node.computeHeuristic(action))))
                .sorted(Comparator.comparingDouble(Pair::getValue))
                .limit(Math.min(kBest, actions.size()))
                .collect(Collectors.toList());
        var distribution = new EnumeratedDistribution<>(actionScores);
        return distribution.sample();
    }
}
