package at.ac.tuwien.ifs.sge.agent.alpharisk.domain.heuristics.utility;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;

public interface StateUtilityHeuristic {

    public double calc(RiskState state, int playerID);
}
