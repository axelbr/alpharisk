package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.heuristics.utility;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;

public interface StateUtilityHeuristic {

    public double calc(RiskState state, int playerID);
}
