package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.rollout;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.Policy;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public interface RolloutPolicy extends Policy<RiskState, RiskAction> {
}
