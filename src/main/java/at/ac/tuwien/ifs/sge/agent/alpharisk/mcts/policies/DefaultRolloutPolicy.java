package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.heuristics.selection.ActionSelectionHeuristic;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.heuristics.utility.StateUtilityHeuristic;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.Util;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultRolloutPolicy implements RolloutPolicy {

    private final Map<RiskState.Phase, ActionSelectionHeuristic> heuristics;
    private StateUtilityHeuristic stateUtilityHeuristic;

    public DefaultRolloutPolicy(Map<RiskState.Phase, ActionSelectionHeuristic> heuristics, StateUtilityHeuristic stateUtilityHeuristic) {

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
    public RiskAction selectAction(RiskState state) {
        var current = state;

        return null; //computeValue(state, node.getState().getCurrentPlayer());
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
