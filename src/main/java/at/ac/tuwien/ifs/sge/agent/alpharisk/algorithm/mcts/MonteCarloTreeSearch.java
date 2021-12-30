package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts;

import at.ac.tuwien.ifs.sge.agent.alpharisk.Phase;
import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.heuristics.selection.BorderSecurityThreatHeuristic;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.heuristics.utility.MaximizeControlledTerritoriesHeuristic;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.backpropagation.BackpropagationStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.backpropagation.NegamaxBackupStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.expansion.DefaultExpansionStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.expansion.ExpansionStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.rollouts.DefaultRolloutPolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.rollouts.RolloutPolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.selection.DefaultTreePolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.selection.TreePolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.stoppingcriterions.MaxIterationsStoppingCriterion;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.stoppingcriterions.StoppingCriterion;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes.NodeFactory;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.Util;
import at.ac.tuwien.ifs.sge.util.tree.DoubleLinkedTree;
import at.ac.tuwien.ifs.sge.util.tree.Tree;

import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class MonteCarloTreeSearch {

    public static class HyperParameters {
        public double explorationConstant = 1.0;
        public int maxIterations = 128;
    }

    private TreePolicy treePolicy;
    private RolloutPolicy rolloutPolicy;
    private ExpansionStrategy expansionStrategy;
    private BackpropagationStrategy backpropagationStrategy;
    private RiskAction currentBestAction;


    public MonteCarloTreeSearch(HyperParameters parameters) {
        this.treePolicy = new DefaultTreePolicy(parameters.explorationConstant);
        this.rolloutPolicy = new DefaultRolloutPolicy(
                Map.of(Phase.REINFORCE, new BorderSecurityThreatHeuristic()),
                new MaximizeControlledTerritoriesHeuristic(),
                new MaxIterationsStoppingCriterion(parameters.maxIterations)
        );
        this.expansionStrategy = new DefaultExpansionStrategy();
        this.backpropagationStrategy = new NegamaxBackupStrategy();
    }

    public MonteCarloTreeSearch(TreePolicy treePolicy, RolloutPolicy rolloutPolicy, ExpansionStrategy expansionStrategy, BackpropagationStrategy backpropagationStrategy) {
        this.treePolicy = treePolicy;
        this.rolloutPolicy = rolloutPolicy;
        this.expansionStrategy = expansionStrategy;
        this.backpropagationStrategy = backpropagationStrategy;
    }

    public void setTreePolicy(TreePolicy treePolicy) {
        this.treePolicy = treePolicy;
    }

    public void setRolloutPolicy(RolloutPolicy rolloutPolicy) {
        this.rolloutPolicy = rolloutPolicy;
    }

    public void setExpansionStrategy(ExpansionStrategy expansionStrategy) {
        this.expansionStrategy = expansionStrategy;
    }

    public void setBackpropagationStrategy(BackpropagationStrategy backpropagationStrategy) {
        this.backpropagationStrategy = backpropagationStrategy;
    }

    public TreePolicy getTreePolicy() {
        return treePolicy;
    }

    public RolloutPolicy getRolloutPolicy() {
        return rolloutPolicy;
    }

    public ExpansionStrategy getExpansionStrategy() {
        return expansionStrategy;
    }

    public BackpropagationStrategy getBackpropagationStrategy() {
        return backpropagationStrategy;
    }

    public RiskAction computeAction(RiskState state, long time, TimeUnit unit) {
        ExecutorService executorService =  Executors.newCachedThreadPool();
        var search = executorService.submit(() -> search(state, new MaxIterationsStoppingCriterion(128)));
        RiskAction action;
        try {
            action = search.get(time, unit);
        } catch (TimeoutException e) {
            action = currentBestAction;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e.getCause());
        }
        return action;
    }

    private RiskAction search(RiskState state, StoppingCriterion stoppingCriterion) {
        Tree<Node> root = new DoubleLinkedTree<>(NodeFactory.makeRoot(state));
        var node = root;
        currentBestAction = Util.selectRandom(state.getGame().getPossibleActions());
        stoppingCriterion.reset();
        while (!stoppingCriterion.shouldStop()) {
            node = treePolicy.apply(root);
            node = expansionStrategy.apply(node);
            double value = rolloutPolicy.apply(node);
            backpropagationStrategy.apply(node, value);
            currentBestAction = getBestAction(root, state.getCurrentPlayer());
        }
        return currentBestAction;
    }

    private RiskAction getBestAction(Tree<Node> node, int playerId) {
        return node.getChildren().stream()
                .map(Tree::getNode)
                .max(Comparator.comparingDouble(Node::getValue))
                .orElseThrow()
                .getAction()
                .orElseThrow();
    }
}
