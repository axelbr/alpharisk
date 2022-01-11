package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.simulation;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.SimulationStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.RolloutPolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;

import java.util.function.Function;

public class LimitedDepthSimulation implements SimulationStrategy {
    private final int maxDepth;
    private final Function<RiskState, Double> valueFunction;

    public LimitedDepthSimulation(int maxDepth, Function<RiskState, Double> stateUtilityFunction) {
        this.maxDepth = maxDepth;
        this.valueFunction = stateUtilityFunction;
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
            value = valueFunction.apply(state);
        }
        return value;
    }
}
