package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.heuristics;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.function.BiFunction;

public interface ActionValueFunction extends BiFunction<RiskState, RiskAction, Double> {
}
