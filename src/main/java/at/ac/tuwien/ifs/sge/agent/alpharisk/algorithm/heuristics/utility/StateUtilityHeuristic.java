package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.heuristics.utility;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;

import java.util.function.Function;

public interface StateUtilityHeuristic {

    public double calc(RiskState state, int playerID);
}
