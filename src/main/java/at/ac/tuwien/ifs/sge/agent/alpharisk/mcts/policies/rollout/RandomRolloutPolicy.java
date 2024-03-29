package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.rollout;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.RiskState;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.Util;

public class RandomRolloutPolicy implements RolloutPolicy {

    @Override
    public RiskAction selectAction(RiskState state) {
        return Util.selectRandom(state.getGame().getPossibleActions());
    }
}