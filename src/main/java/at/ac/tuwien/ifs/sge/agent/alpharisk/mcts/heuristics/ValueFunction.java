package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.heuristics;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;

import java.util.function.Function;

public interface ValueFunction extends Function<RiskState, Double> {
}
