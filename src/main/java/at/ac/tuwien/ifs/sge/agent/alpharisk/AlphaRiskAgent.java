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
        public static double explorationConstant = 2.0;
        public static int numSimulations = 1;
        public static int maxDepth = 100;
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
        //if (this.root == null) {
            this.root = new DoubleLinkedTree<>(NodeFactory.makeRoot(initialState));
        /*} else {
            reRootTree(initialState);
        }*/
        int counter = 0;
        /*if (false && initialState.getPhase() != Phase.REINFORCE) {
            return Util.selectRandom(game.getPossibleActions());
        }*/
        while (!this.shouldStopComputation(2)) {
            var node = treePolicy.apply(root);
            node = expansionStrategy.apply(node);
            if (node != null) {
                for (int i = 0; i < HyperParameters.numSimulations && !this.shouldStopComputation(); i++) {
                    double value = simulationStrategy.apply(node);
                    backpropagationStrategy.apply(node, value);
                }
                counter++;
            }
        }
        log.warn("Expanded " + counter + " nodes.");
        return getBestAction(root, initialState.getCurrentPlayer());
    }

    private RiskAction getBestAction(Tree<Node> tree, int playerId) {
        if(tree.getChildren().isEmpty()){
            log.error("Node has no Children!");
        }

        Node n = tree.getChild(0).getNode();
        Node max_n = n;
        var max_value = -Double.MAX_VALUE;
        for (Tree<Node> child : tree.getChildren()) {
            n = child.getNode();
            var val = Util.percentage(n.getValue(), n.getPlays());
            if (val > max_value) {
                max_value = val;
                max_n = n;
            }
            log.info(
                "Action: " + n.getAction().orElseThrow() + " " + String.format("%.2f",val)
                + " (" + String.format("%.2f",n.getValue()) + "/" + n.getPlays()
                + " plays)");
        }
        var action = max_n.getAction().orElseThrow();
        log.warn("Best Action: " + action + " with value " + String.format("%.2f",max_value) + " (" + String.format("%.2f",max_n.getValue())+"/" + max_n
            .getPlays() + " plays)");
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
