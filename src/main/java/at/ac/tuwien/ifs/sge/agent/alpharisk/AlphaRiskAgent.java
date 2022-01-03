package at.ac.tuwien.ifs.sge.agent.alpharisk;

import at.ac.tuwien.ifs.sge.agent.AbstractGameAgent;
import at.ac.tuwien.ifs.sge.agent.GameAgent;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.backpropagation.NegamaxBackupStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.expansion.ExpandSingleNodeStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.selection.UCTSelection;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.simulation.RandomSimulationStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.stoppingcriterions.MaxIterationsStoppingCriterion;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes.NodeFactory;
import at.ac.tuwien.ifs.sge.engine.Logger;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.Util;
import at.ac.tuwien.ifs.sge.util.tree.DoubleLinkedTree;
import at.ac.tuwien.ifs.sge.util.tree.Tree;

import java.util.Comparator;
import java.util.concurrent.*;

public class AlphaRiskAgent extends AbstractGameAgent<Risk, RiskAction> implements GameAgent<Risk, RiskAction>
{

    public static class HyperParameters {
        public double explorationConstant = 1.0;
        public int maxIterations = 128;
    }

    private final UCTSelection treePolicy;
    private final RandomSimulationStrategy simulationStrategy;
    private final ExpandSingleNodeStrategy expansionStrategy;
    private final NegamaxBackupStrategy backpropagationStrategy;
    private Phase currentPhase;
     private DoubleLinkedTree<Node> root;

    public AlphaRiskAgent(final Logger log) {
        super(0.75, 5L, TimeUnit.SECONDS, log);
        currentPhase = Phase.INITIAL_SELECT;
        this.treePolicy = new UCTSelection(new HyperParameters().explorationConstant);
        this.simulationStrategy = new RandomSimulationStrategy(new MaxIterationsStoppingCriterion(256));
        this.expansionStrategy = new ExpandSingleNodeStrategy();
        this.backpropagationStrategy = new NegamaxBackupStrategy();
    }
    
    public void setUp(final int numberOfPlayers, final int playerId) {
        super.setUp(numberOfPlayers, playerId);
    }
    
    public RiskAction computeNextAction(final Risk game, final long computationTime, final TimeUnit timeUnit) {
        super.setTimers(computationTime, timeUnit);
        currentPhase = currentPhase.update(game);
        RiskState initialState = new RiskState(game, currentPhase);
        this.root = new DoubleLinkedTree<>(NodeFactory.makeRoot(initialState));
        var currentBestAction = Util.selectRandom(game.getPossibleActions());
        while (!this.shouldStopComputation()) {
            var node = treePolicy.apply(root);
            node = expansionStrategy.apply(node);
            double value = simulationStrategy.apply(node);
            backpropagationStrategy.apply(node, value);
            currentBestAction = getBestAction(root, initialState.getCurrentPlayer());
        }
        return currentBestAction;
    }

    private RiskAction getBestAction(Tree<Node> node, int playerId) {
        var action = node.getChildren().stream()
                .map(Tree::getNode)
                .max(Comparator.comparingDouble(Node::getValue))
                .orElseThrow()
                .getAction()
                .orElseThrow();
        return action;
    }

    private void reRootTree(RiskState state) {
        int currentNumberOfActions = root.getNode().getState().getGame().getActionRecords().size();
        var actionRecords = state.getGame().getActionRecords();
        Tree<Node> current = root;
        for (int i = currentNumberOfActions; i < actionRecords.size(); i++) {
            var action = actionRecords.get(i);
            var playedBranch = current.getChildren().stream()
                    .filter(c -> c.getNode().getAction().get().equals(action.getAction()))
                    .findAny();
            if (playedBranch.isPresent()) {
                current = playedBranch.get();
            } else {
                this.root = new DoubleLinkedTree<>(NodeFactory.makeRoot(state));
                return;
            }
        }
        root.reRoot(current);
        root.dropParent();
    }
}
