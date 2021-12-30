package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.heuristics.utility;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;

import java.util.function.Function;

public interface StateUtilityHeuristic extends Function<RiskState, Double> {
}
