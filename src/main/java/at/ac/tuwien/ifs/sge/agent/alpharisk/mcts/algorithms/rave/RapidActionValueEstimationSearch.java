package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.algorithms.rave;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.algorithms.DefaultMonteCarloTreeSearch;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.treepolicies.HeuristicUCTPolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.simulation.LimitedDepthSimulation;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.NodeStatistics;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.pair.Pair;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;

import javax.swing.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RapidActionValueEstimationSearch extends DefaultMonteCarloTreeSearch {

    private final Configuration configuration;

    public RapidActionValueEstimationSearch(Configuration configuration) {
        super();
        this.configuration = configuration;
        setSimulationStrategy(new LimitedDepthSimulation(32));
        setTreePolicy(new HeuristicUCTPolicy(0.5));
    }

    public static Configuration getDefaultConfiguration() {
        var config = new BaseConfiguration();
        config.setProperty("explorationConstant", 0.5);
        config.setProperty("rolloutHorizon", 32);
        config.setProperty("bias", 0.5);
        return config;
    }

    public Function<Node, Node> nodeConstructor() {
        return n -> new RaveNode(n, configuration.getDouble("bias"));
    }

    @Override
    public void runIteration(Node root) {
        super.runIteration(new RaveNode(root, configuration.getDouble("bias")));
    }

    @Override
    public void backup(Node node, List<Pair<RiskState, RiskAction>> playout) {
        var playerActions = computePlayerActions(playout);

        var current = node;
        var lastState = playout.get(playout.size() - 1).getA();
        var currentPlayer = node.getState().getCurrentPlayer();
        var currentValue = lastState.getGame().getUtilityValue(currentPlayer);

        var statistics = NodeStatistics.of(RaveNode.AMAF_VALUE, currentValue);

        while (!current.isRoot()) {
            if (current.getState().getCurrentPlayer() != currentPlayer) {
                currentValue = 1.0 - currentValue;
                currentPlayer = current.getState().getCurrentPlayer();
            }

            for (Node sibling: getPlayedActionSiblings(current, playerActions.get(currentPlayer))) {
                sibling.update(statistics);
            }
            current.update(statistics.concat(RaveNode.VALUE, currentValue));
            current = current.getParent();
        }
    }


    private Map<Integer, Set<RiskAction>> computePlayerActions(List<Pair<RiskState, RiskAction>> playout) {
        Map<Integer, Set<RiskAction>> playerActions = new HashMap<>();
        for (Pair<RiskState, RiskAction> stateActionPair: playout.subList(0, playout.size() - 2)) {
            var state = stateActionPair.getA();
            var action = stateActionPair.getB();
            if (!playerActions.containsKey(state.getCurrentPlayer())) {
                playerActions.put(state.getCurrentPlayer(), new HashSet<>());
            }
            playerActions.get(state.getCurrentPlayer()).add(action);
        }
        return playerActions;
    }

    private List<Node> getPlayedActionSiblings(Node node, Set<RiskAction> actions) {
        return node.getParent().expandedChildren().stream()
                .filter(n -> !n.getAction().equals(node.getAction()))
                .filter(n -> n.getState().getCurrentPlayer() == node.getState().getCurrentPlayer())
                .filter(n -> actions.contains(n.getAction()))
                .collect(Collectors.toList());
    }
}
