package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.simulation;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.Policy;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.pair.Pair;

import java.util.List;

public interface SimulationStrategy {
    List<Pair<RiskState, RiskAction>> simulate(RiskState state, Policy<RiskState, RiskAction> policy);
}
