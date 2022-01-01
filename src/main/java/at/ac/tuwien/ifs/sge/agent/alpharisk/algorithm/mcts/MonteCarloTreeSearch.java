package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts;

import at.ac.tuwien.ifs.sge.agent.alpharisk.Phase;
import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.heuristics.selection.BorderSecurityThreatHeuristic;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.heuristics.utility.MaximizeControlledTerritoriesHeuristic;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.backpropagation.BackpropagationStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.backpropagation.NegamaxBackupStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.expansion.ExpandAllSelectRandom;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.expansion.ExpansionStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.simulation.DefaultSimulationStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.simulation.SimulationStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.selection.UCTSelection;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.selection.TreePolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.stoppingcriterions.MaxIterationsStoppingCriterion;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.stoppingcriterions.StoppingCriterion;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes.NodeFactory;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.Util;
import at.ac.tuwien.ifs.sge.util.tree.DoubleLinkedTree;
import at.ac.tuwien.ifs.sge.util.tree.Tree;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.*;

public class MonteCarloTreeSearch {

    public static class HyperParameters {
        public double explorationConstant = 1.0;
        public int maxIterations = 128;
    }

    private TreePolicy treePolicy;
    private SimulationStrategy simulationStrategy;
    private ExpansionStrategy expansionStrategy;
    private BackpropagationStrategy backpropagationStrategy;
    private RiskAction currentBestAction;


    public MonteCarloTreeSearch(HyperParameters parameters) {
        this.treePolicy = new UCTSelection(parameters.explorationConstant);
        this.simulationStrategy = new DefaultSimulationStrategy(
                Map.of(Phase.REINFORCE, new BorderSecurityThreatHeuristic()),
                new MaximizeControlledTerritoriesHeuristic(),
                new MaxIterationsStoppingCriterion(parameters.maxIterations)
        );
        this.expansionStrategy = new ExpandAllSelectRandom();
        this.backpropagationStrategy = new NegamaxBackupStrategy();
    }

    public MonteCarloTreeSearch(TreePolicy treePolicy, SimulationStrategy simulationStrategy, ExpansionStrategy expansionStrategy, BackpropagationStrategy backpropagationStrategy) {
        this.treePolicy = treePolicy;
        this.simulationStrategy = simulationStrategy;
        this.expansionStrategy = expansionStrategy;
        this.backpropagationStrategy = backpropagationStrategy;
    }

    public void setTreePolicy(TreePolicy treePolicy) {
        this.treePolicy = treePolicy;
    }

    public void setSimulationStrategy(SimulationStrategy simulationStrategy) {
        this.simulationStrategy = simulationStrategy;
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

    public SimulationStrategy getSimulationStrategy() {
        return simulationStrategy;
    }

    public ExpansionStrategy getExpansionStrategy() {
        return expansionStrategy;
    }

    public BackpropagationStrategy getBackpropagationStrategy() {
        return backpropagationStrategy;
    }

    public RiskAction computeAction(RiskState state) {
        Tree<Node> root = new DoubleLinkedTree<>(NodeFactory.makeRoot(state));
        var node = root;
        currentBestAction = Util.selectRandom(state.getGame().getPossibleActions());
        var stoppingCriterion = new MaxIterationsStoppingCriterion(128);
        while (!stoppingCriterion.shouldStop()) {
            node = treePolicy.apply(root);
            node = expansionStrategy.apply(node);
            double value = simulationStrategy.apply(node);
            backpropagationStrategy.apply(node, value);
            currentBestAction = getBestAction(root, state.getCurrentPlayer());
        }
        return currentBestAction;
    }

    public RiskAction getCurrentBestAction() {
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
