package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.heuristics.selection;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.function.Function;

public interface ActionSelectionHeuristic extends Function<RiskState, RiskAction> {
}
