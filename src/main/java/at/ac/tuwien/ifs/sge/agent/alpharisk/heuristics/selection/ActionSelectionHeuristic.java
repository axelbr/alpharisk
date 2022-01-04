package at.ac.tuwien.ifs.sge.agent.alpharisk.heuristics.selection;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.function.Function;

public interface ActionSelectionHeuristic extends Function<RiskState, RiskAction> {
}
