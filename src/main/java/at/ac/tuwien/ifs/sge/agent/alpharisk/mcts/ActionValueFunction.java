package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.RiskState;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.function.BiFunction;

public interface ActionValueFunction extends BiFunction<RiskState, RiskAction, Double> {
}
