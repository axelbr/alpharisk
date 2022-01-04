package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.simulation;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.heuristics.selection.ActionSelectionHeuristic;
import at.ac.tuwien.ifs.sge.agent.alpharisk.heuristics.utility.StateUtilityHeuristic;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.stoppingcriterions.StoppingCriterion;
import at.ac.tuwien.ifs.sge.agent.alpharisk.nodes.Node;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.Util;
import at.ac.tuwien.ifs.sge.util.tree.Tree;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultSimulationStrategy implements SimulationStrategy {

    private final StoppingCriterion stoppingCriterion;
    private final Map<RiskState.Phase, ActionSelectionHeuristic> heuristics;
    private StateUtilityHeuristic stateUtilityHeuristic;

    public DefaultSimulationStrategy(Map<RiskState.Phase, ActionSelectionHeuristic> heuristics, StateUtilityHeuristic stateUtilityHeuristic, StoppingCriterion stoppingCriterion) {
        this.stoppingCriterion = stoppingCriterion;
        this.heuristics = heuristics;
        this.stateUtilityHeuristic = stateUtilityHeuristic;
    }

    public void setHeuristic(RiskState.Phase phase, ActionSelectionHeuristic heuristic) {
        heuristics.put(phase, heuristic);
    }

    public void  setStateUtilityHeuristic(StateUtilityHeuristic heuristic) {
        this.stateUtilityHeuristic = heuristic;
    }

    @Override
    public Double apply(Tree<Node> tree) {
        var state = tree.getNode().getState();
        stoppingCriterion.reset();
        while (!stoppingCriterion.shouldStop() && state.getPhase() != RiskState.Phase.TERMINATED) {
            var action = determineAction(state);
            state = state.apply(action);
        }
        return computeValue(state, tree.getNode().getState().getCurrentPlayer());
    }

    private Double computeValue(RiskState state, int playerId) {
        return stateUtilityHeuristic.calc(state, playerId);
    }

    private RiskAction determineAction(RiskState state) {
        RiskAction action;
        action = Util.selectRandom(state.getGame().getPossibleActions());
        return action;
    }

    private RiskAction sampleAction(Node node, int kBest) {
        var actions = node.getState().getGame().getPossibleActions();
        var heuristic = heuristics.get(node.getState().getPhase());
        var actionScores = actions.stream()
                .map(action -> new Pair<>(action, Math.exp(1))) // TODO: use heuristic score
                .sorted(Comparator.comparingDouble(Pair::getValue))
                .limit(Math.min(kBest, actions.size()))
                .collect(Collectors.toList());
        var distribution = new EnumeratedDistribution<>(actionScores);
        return distribution.sample();
    }
}
