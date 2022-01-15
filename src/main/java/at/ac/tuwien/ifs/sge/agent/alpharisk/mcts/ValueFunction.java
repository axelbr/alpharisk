package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.RiskState;

@FunctionalInterface
public interface ValueFunction {
    double evaluate(RiskState state);
}
