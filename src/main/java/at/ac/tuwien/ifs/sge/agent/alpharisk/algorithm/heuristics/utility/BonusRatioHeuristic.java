package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.heuristics.utility;

import java.util.stream.IntStream;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;

public class BonusRatioHeuristic implements StateUtilityHeuristic {

    public double calc(RiskState state, int playerID) {
        var board = state.getBoard();
        return (double)state.computeBonus(playerID) / IntStream.range(0, board.getNumberOfPlayers()).map(state::computeBonus).sum();
    }
}
