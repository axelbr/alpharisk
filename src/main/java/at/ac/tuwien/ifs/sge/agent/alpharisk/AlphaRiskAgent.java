package at.ac.tuwien.ifs.sge.agent.alpharisk;

import java.util.concurrent.TimeUnit;

import at.ac.tuwien.ifs.sge.agent.AbstractGameAgent;
import at.ac.tuwien.ifs.sge.agent.GameAgent;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.backpropagation.BackpropagationStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.backpropagation.NegamaxBackupStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.expansion.ExpandAllSelectRandom;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.expansion.ExpansionStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.selection.TreePolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.selection.UCTSelection;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.simulation.RandomSimulationStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.simulation.SimulationStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.stoppingcriterions.MaxIterationsStoppingCriterion;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes.NodeFactory;
import at.ac.tuwien.ifs.sge.engine.Logger;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.Util;
import at.ac.tuwien.ifs.sge.util.tree.DoubleLinkedTree;
import at.ac.tuwien.ifs.sge.util.tree.Tree;

public class AlphaRiskAgent extends AbstractGameAgent<Risk, RiskAction> implements GameAgent<Risk, RiskAction> {

    public static class HyperParameters {
        public double explorationConstant = 1.0;
        public int maxPlayouts = 256;
    }

    private final TreePolicy treePolicy;
    private final SimulationStrategy simulationStrategy;
    private final ExpansionStrategy expansionStrategy;
    private final BackpropagationStrategy backpropagationStrategy;
    private Phase currentPhase;
    private DoubleLinkedTree<Node> root;

    public AlphaRiskAgent(final Logger log) {
        super(0.75, 5L, TimeUnit.SECONDS, log);
        currentPhase = Phase.INITIAL_SELECT;
        var params = new HyperParameters();
        this.treePolicy = new UCTSelection(params.explorationConstant);
        this.simulationStrategy = new RandomSimulationStrategy(new MaxIterationsStoppingCriterion(params.maxPlayouts));
        this.expansionStrategy = new ExpandAllSelectRandom();
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
        int counter = 0;
        if (false && initialState.getPhase() != Phase.REINFORCE) {
            return Util.selectRandom(game.getPossibleActions());
        }
        while (!this.shouldStopComputation()) {
            var node = treePolicy.apply(root);
            node = expansionStrategy.apply(node);
            if (node != null) {
                double value = simulationStrategy.apply(node);
                backpropagationStrategy.apply(node, value);
                counter++;
            }
        }
        log.warn("Expanded " + counter + " nodes.");
        return getBestAction(root, initialState.getCurrentPlayer());
    }

    private RiskAction getBestAction(Tree<Node> node, int playerId) {
        Node n = node.getChild(0).getNode();
        var action = n.getAction().orElseThrow();
        var max_value = n.getValue() / n.getPlays();
        for (Tree<Node> child : node.getChildren()) {
            n = child.getNode();
            var val = n.getValue() / n.getPlays();
            if (val > max_value) {
                max_value = val;
                action = n.getAction().orElseThrow();
            }
        }
        log.warn("Best Action: " + action + " with value " + max_value);
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
