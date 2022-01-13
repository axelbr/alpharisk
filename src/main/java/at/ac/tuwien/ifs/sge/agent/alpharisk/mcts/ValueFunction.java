package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;

import java.util.function.Function;

@FunctionalInterface
public interface ValueFunction {
    double evaluate(RiskState state);
}
