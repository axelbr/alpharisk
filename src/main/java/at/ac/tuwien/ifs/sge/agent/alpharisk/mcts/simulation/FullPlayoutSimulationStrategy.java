package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.simulation;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.Policy;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.pair.ImmutablePair;
import at.ac.tuwien.ifs.sge.util.pair.Pair;

import java.util.ArrayList;
import java.util.List;

public class FullPlayoutSimulationStrategy implements SimulationStrategy {
    @Override
    public List<Pair<RiskState, RiskAction>> simulate(RiskState state, Policy<RiskState, RiskAction> policy) {
        var current = state;
        List<Pair<RiskState, RiskAction>> trajectory = new ArrayList<>();
        while (!state.getGame().isGameOver()) {
            var action = policy.selectAction(state);
            trajectory.add(new ImmutablePair<>(current, action));
            current = current.apply(action);
        }
        trajectory.add(new ImmutablePair<>(current, null));
        return trajectory;
    }
}
