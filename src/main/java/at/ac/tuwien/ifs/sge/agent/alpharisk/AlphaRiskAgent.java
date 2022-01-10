package at.ac.tuwien.ifs.sge.agent.alpharisk;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import at.ac.tuwien.ifs.sge.agent.AbstractGameAgent;
import at.ac.tuwien.ifs.sge.agent.GameAgent;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.backpropagation.BackpropagationStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.backpropagation.NegamaxBackupStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.selection.TreePolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.selection.UCTSelection;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.simulation.RandomSimulationStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.simulation.SimulationStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.stoppingcriterions.MaxIterationsStoppingCriterion;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.NodeFactories;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.TreeVisualization;
import at.ac.tuwien.ifs.sge.engine.Logger;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.Util;
import at.ac.tuwien.ifs.sge.util.tree.Tree;
import guru.nidi.graphviz.engine.Format;

public class AlphaRiskAgent extends AbstractGameAgent<Risk, RiskAction> implements GameAgent<Risk, RiskAction> {


    public static class HyperParameters {
        public static double explorationConstant = 0.5;
        public static int maxDepth = 200;
    }

    private final TreePolicy treePolicy;
    private final SimulationStrategy simulationStrategy;
    private final BackpropagationStrategy backpropagationStrategy;
    private RiskState.Phase currentPhase;
    private Node root;

    public AlphaRiskAgent(final Logger log) {
        super(0.75, 5L, TimeUnit.SECONDS, log);
        currentPhase = RiskState.Phase.INITIAL_SELECT;
        this.treePolicy = new UCTSelection(HyperParameters.explorationConstant);
        this.simulationStrategy =
            new RandomSimulationStrategy(new MaxIterationsStoppingCriterion(HyperParameters.maxDepth));
        this.backpropagationStrategy = new NegamaxBackupStrategy();
        NodeFactories.setTreePolicy(treePolicy);
    }

    public void setUp(final int numberOfPlayers, final int playerId) {
        super.setUp(numberOfPlayers, playerId);
    }

    public RiskAction computeNextAction(final Risk game, final long computationTime, final TimeUnit timeUnit) {
        super.setTimers(computationTime, timeUnit);
        currentPhase = currentPhase.update(game);
        RiskState initialState = new RiskState(game, currentPhase);
        this.root = NodeFactories.decisionNodeFactory().makeRoot(initialState);
        int counter = 0;

        if (initialState.getPhase() != RiskState.Phase.ATTACK) {
           return Util.selectRandom(initialState.getGame().getPossibleActions());
        }

        while (!this.shouldStopComputation()) {
            var node = select(root);
            if (node == null) {
                break;
            }
            node = node.expand();
            if (node != null) {
                double value = simulationStrategy.apply(node);
                backpropagationStrategy.apply(node, value);

            }
            counter++;
            TreeVisualization.save(root, String.format("./searchtree-%d.dot", counter), Format.DOT);
        }
        log.info(String.format("Run %d iterations. Expanded nodes: %d.", counter, root.size()));
        return getBestAction(root, initialState.getCurrentPlayer());
    }

    private Node select(Node root) {
        var current = root;
        while (current != null && current.isFullyExpanded()) {
            current = current.select();
        }
        return current;
    }

    private RiskAction getBestAction(Node node, int playerId) {
        if(node.isLeaf()){
            return null;
        }
        Node max_n = node;
        var max_value = -Double.MAX_VALUE;
        for (var child : node.getChildren()) {
            if (child.getValue() > max_value) {
                max_value = child.getValue();
                max_n = child;
            }
            log.info(String.format("Action: %s, Value: %.2f, Visits: %d", child.getAction().orElseThrow(), child.getValue(), child.getVisits()));
        }
        var action = max_n.getAction().orElseThrow();
        log.info(String.format("Action: %s, Value: %.2f, Visits: %d", max_n.getAction().orElseThrow(), max_n.getValue(), max_n.getVisits()));
        return action;
    }

    private Node rerootTree(Node root, RiskState newState) {
        Node newRoot = NodeFactories.decisionNodeFactory().makeRoot(newState);
        if (root == null) {
            return newRoot;
        }
        var actions = newState.getGame().getActionRecords().stream()
                .skip(root.getState().getGame().getNumberOfActions())
                .collect(Collectors.toList());
        var current = root;
        for (var action: actions) {
            var selection = current.select(action.getAction());
            if (selection.isPresent()) {
                current = selection.get();
            } else {
                current = null;
                break;
            }
        }
        if (current != null) {
            newRoot = current;
        }
        return newRoot;
    }


}
