package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.simulation;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.Policy;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.pair.ImmutablePair;
import at.ac.tuwien.ifs.sge.util.pair.Pair;

import java.util.ArrayList;
import java.util.List;

public class LimitedDepthSimulation implements SimulationStrategy {
    private final int maxDepth;

    public LimitedDepthSimulation(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Override
    public List<Pair<RiskState, RiskAction>> simulate(RiskState state, Policy<RiskState, RiskAction> policy) {
        var current = state;
        List<Pair<RiskState, RiskAction>> trajectory = new ArrayList<>();
        int t = 0;
        while (!current.getGame().isGameOver() && t < maxDepth) {
            var action = policy.selectAction(current);
            trajectory.add(new ImmutablePair<>(current, action));
            current = current.apply(action);
            t++;
        }
        trajectory.add(new ImmutablePair<>(current, null));
        return trajectory;
    }
}
