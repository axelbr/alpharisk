package at.ac.tuwien.ifs.sge.agent.alpharisk;

import java.util.concurrent.TimeUnit;

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
import at.ac.tuwien.ifs.sge.engine.Logger;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.Util;

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

        if (initialState.getPhase() == RiskState.Phase.INITIAL_REINFORCE || initialState.getPhase() == RiskState.Phase.INITIAL_SELECT) {
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

        Node n = null;
        Node max_n = n;
        var max_value = -Double.MAX_VALUE;
        for (var child : node.getChildren()) {
            n = child;
            var val = Util.percentage(n.getValue(), n.getVisits());
            if (val > max_value) {
                max_value = val;
                max_n = n;
            }
            log.info(String.format("Action: %s %.2f (%.2f/%d plays)", n.getAction().orElseThrow(), val, n.getValue(), n.getVisits()));
        }
        var action = max_n.getAction().orElseThrow();
        log.info(String.format("Best Action: %s with value %.2f (%.2f/%d plays)", max_n.getAction().orElseThrow(), max_value, max_n.getValue(), max_n.getVisits()));
        return action;
    }


}
