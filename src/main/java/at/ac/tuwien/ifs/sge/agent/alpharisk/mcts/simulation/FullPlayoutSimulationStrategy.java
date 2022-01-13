package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.simulation;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.Policy;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public class FullPlayoutSimulationStrategy implements SimulationStrategy {
    @Override
    public double simulate(RiskState state, Policy<RiskState, RiskAction> policy) {
        var current = state;
        while (!state.getGame().isGameOver()) {
            var action = policy.selectAction(state);
            current = current.apply(action);
        }
        return current.getGame().getUtilityValue(state.getCurrentPlayer());
    }
}
