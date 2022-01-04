package at.ac.tuwien.ifs.sge.agent.alpharisk;

import java.util.concurrent.TimeUnit;

import at.ac.tuwien.ifs.sge.agent.AbstractGameAgent;
import at.ac.tuwien.ifs.sge.agent.GameAgent;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.backpropagation.BackpropagationStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.backpropagation.NegamaxBackupStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.expansion.ExpandAllSelectRandom;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.expansion.ExpansionStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.selection.TreePolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.selection.UCTSelection;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.simulation.RandomSimulationStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.simulation.SimulationStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.stoppingcriterions.MaxIterationsStoppingCriterion;
import at.ac.tuwien.ifs.sge.agent.alpharisk.nodes.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.nodes.NodeFactory;
import at.ac.tuwien.ifs.sge.engine.Logger;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.Util;
import at.ac.tuwien.ifs.sge.util.tree.DoubleLinkedTree;
import at.ac.tuwien.ifs.sge.util.tree.Tree;

public class AlphaRiskAgent extends AbstractGameAgent<Risk, RiskAction> implements GameAgent<Risk, RiskAction> {

    public static class HyperParameters {
        public static double explorationConstant = 0.5;
        public static int maxDepth = 200;
    }

    private final TreePolicy treePolicy;
    private final SimulationStrategy simulationStrategy;
    private final ExpansionStrategy expansionStrategy;
    private final BackpropagationStrategy backpropagationStrategy;
    private RiskState.Phase currentPhase;
    private DoubleLinkedTree<Node> root;

    public AlphaRiskAgent(final Logger log) {
        super(0.75, 5L, TimeUnit.SECONDS, log);
        currentPhase = RiskState.Phase.INITIAL_SELECT;
        this.treePolicy = new UCTSelection(HyperParameters.explorationConstant);
        this.simulationStrategy =
            new RandomSimulationStrategy(new MaxIterationsStoppingCriterion(HyperParameters.maxDepth));
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
        while (!this.shouldStopComputation()) {
            var node = treePolicy.apply(root);
            node = expansionStrategy.apply(node);
            if (node != null) {
                double value = simulationStrategy.apply(node);
                backpropagationStrategy.apply(node, value);
                counter++;
            }
        }
        log.info("Expanded " + counter + " nodes.");
        return getBestAction(root, initialState.getCurrentPlayer());
    }

    private RiskAction getBestAction(Tree<Node> node, int playerId) {
        if(node.getChildren().isEmpty()){return null;}

        Node n = node.getChild(0).getNode();
        Node max_n = n;
        var max_value = -Double.MAX_VALUE;
        for (Tree<Node> child : node.getChildren()) {
            n = child.getNode();
            var val = Util.percentage(n.getValue(), n.getPlays());
            if (val > max_value) {
                max_value = val;
                max_n = n;
            }
            log.info(String.format("Action: %s %.2f (%.2f/%d plays)", n.getAction().orElseThrow(), val, n.getValue(), n.getPlays()));
        }
        var action = max_n.getAction().orElseThrow();
        log.info(String.format("Best Action: %s with value %.2f (%.2f/%d plays)", max_n.getAction().orElseThrow(), max_value, max_n.getValue(), max_n.getPlays()));
        return action;
    }

    private void reRootTree(RiskState state) {
        int currentNumberOfActions = root.getNode().getState().getGame().getActionRecords().size();
        var actionRecords = state.getGame().getActionRecords();
        Tree<Node> current = root;
        for (int i = currentNumberOfActions; i < actionRecords.size(); i++) {
            var action = actionRecords.get(i);
            var playedBranch = current.getChildren().stream()
                .filter(c -> c.getNode().getAction().orElseThrow().equals(action.getAction()))
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
        log.debug("Rerooted tree with "+root.size()+" nodes.");
    }
}
