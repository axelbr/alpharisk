package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.simulation;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.SimulationStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.heuristics.ValueFunction;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.RolloutPolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import org.apache.commons.math3.distribution.BinomialDistribution;

import java.util.Random;
import java.util.function.Function;

public class LimitedDepthSimulation implements SimulationStrategy {
    private final int maxDepth;
    private final ValueFunction valueFunction;

    public LimitedDepthSimulation(int maxDepth, ValueFunction stateUtilityFunction) {
        this.maxDepth = maxDepth;
        this.valueFunction = stateUtilityFunction;
    }

    public LimitedDepthSimulation(int maxDepth) {
        this(maxDepth, s -> 0.0);
    }

    @Override
    public double simulate(Node node, RolloutPolicy policy) {
        var state = node.getState();
        int iterations = 0;
        while (!state.getGame().isGameOver() && iterations < maxDepth) {
            var action = policy.selectAction(state);
            state = state.apply(action);
            iterations++;
        }
        double value;
        if (state.getPhase() == RiskState.Phase.TERMINATED) {
            value = state.hasWon() ? 1.0 : 0.0;
        } else {
            var winProbability = valueFunction.evaluate(state);
            value = Math.random() < winProbability ? 1.0 : 0.0;
        }
        return value;
    }
}
