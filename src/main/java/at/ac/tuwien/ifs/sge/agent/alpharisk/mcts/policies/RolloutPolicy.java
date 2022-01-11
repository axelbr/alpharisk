package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.Policy;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public interface RolloutPolicy extends Policy<RiskState, RiskAction> {
}
