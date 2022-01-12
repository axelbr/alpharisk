package at.ac.tuwien.ifs.sge.agent.alpharisk;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import at.ac.tuwien.ifs.sge.agent.AbstractGameAgent;
import at.ac.tuwien.ifs.sge.agent.GameAgent;
import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.StateFactory;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.heuristics.StateHeuristics;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.heuristics.ValueFunction;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.HeuristicUCTPolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.RandomRolloutPolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.simulation.LimitedDepthSimulation;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.NodeFactories;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.SearchTree;
import at.ac.tuwien.ifs.sge.agent.alpharisk.visualization.BoardVisualization;
import at.ac.tuwien.ifs.sge.agent.alpharisk.visualization.TreeVisualization;
import at.ac.tuwien.ifs.sge.engine.Logger;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.Util;
import guru.nidi.graphviz.engine.Format;

public class AlphaRiskAgent extends AbstractGameAgent<Risk, RiskAction> implements GameAgent<Risk, RiskAction> {


    private final Function<Node, SearchTree> searchTreeBuilder;

    public static class HyperParameters {
        public double explorationConstant = 0.5;
        public int maxRolloutDepth = 16;
    }

    private final HyperParameters parameters;
    private RiskState.Phase currentPhase;
    private SearchTree searchTree;

    public AlphaRiskAgent(final Logger log) {
        super(0.75, 5L, TimeUnit.SECONDS, log);
        currentPhase = RiskState.Phase.INITIAL_SELECT;
        parameters = new HyperParameters();
        var treePolicy = new HeuristicUCTPolicy(parameters.explorationConstant, StateHeuristics.territoryRatioHeuristic());
        var rolloutPolicy = new RandomRolloutPolicy();
        var simulationStrategy = new LimitedDepthSimulation(parameters.maxRolloutDepth, StateHeuristics.territoryRatioHeuristic());
        this.searchTreeBuilder = SearchTree.getSearchTreeBuilder(rolloutPolicy, treePolicy, simulationStrategy);
    }

    public void setUp(final int numberOfPlayers, final int playerId) {
        super.setUp(numberOfPlayers, playerId);
    }

    public RiskAction computeNextAction(final Risk game, final long computationTime, final TimeUnit timeUnit) {
        super.setTimers(computationTime, timeUnit);
        searchTree = updateSearchTree(game);
        int counter = 0;

        if (currentPhase == RiskState.Phase.INITIAL_REINFORCE) {
            return Util.selectRandom(game.getPossibleActions());
        }

        while (!this.shouldStopComputation()) {
            var node = searchTree.select();
            var expanded = searchTree.expand(node);
            if (expanded != null) {
                searchTree.rollout(expanded);
            }
            counter++;
            if (counter % 16 == 0) {
                TreeVisualization.save(searchTree.getRoot(), String.format("../logs/searchtree-%d.dot", counter), Format.DOT);
            }
        }
        BoardVisualization.saveBoard(String.format("../logs/current_board.svg", game.getNumberOfActions()), searchTree.getRoot().getState());
        log.info(String.format("Run %d iterations. Expanded nodes: %d.", counter, searchTree.size()));
        var action = getBestAction(searchTree.getRoot());
        return action;
    }


    private SearchTree updateSearchTree(Risk game) {
        currentPhase = currentPhase.update(game);
        RiskState initialState = StateFactory.make(game, currentPhase);
        if (searchTree == null) {
            var root = NodeFactories.makeRoot(initialState);
            return searchTreeBuilder.apply(root);
        } else {
            return searchTree.findRoot(initialState);
        }
    }

    private RiskAction getBestAction(Node node) {
        if(node.isLeaf()){
            return null;
        }
        Node max_n = node;
        var max_value = -Double.MAX_VALUE;
        for (var child : node.expandedChildren()) {
            if (child.getValue() > max_value) {
                max_value = child.getValue();
                max_n = child;
            }
            log.info(String.format("Action: %s, Value: %.2f, Visits: %d", child.getAction(), child.getValue(), child.getVisits()));
        }
        var action = max_n.getAction();
        log.info(String.format("Best Action: %s, Value: %.2f, Visits: %d", max_n.getAction(), max_n.getValue(), max_n.getVisits()));
        return action;
    }
}
