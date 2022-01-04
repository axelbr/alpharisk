package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.heuristics;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;

import java.util.function.Function;

public interface ActionValueHeuristic extends Function<RiskState, Double> {
    @Override
    default Double apply(RiskState state) {
        return 0.0;
    }
}
