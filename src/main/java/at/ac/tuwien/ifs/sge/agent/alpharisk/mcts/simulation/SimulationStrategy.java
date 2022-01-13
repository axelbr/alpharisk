package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.simulation;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.Policy;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public interface SimulationStrategy {
    double simulate(RiskState state, Policy<RiskState, RiskAction> policy);
}
