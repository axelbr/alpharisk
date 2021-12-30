package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.heuristics.utility;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;

public class MaximizeControlledTerritoriesHeuristic implements StateUtilityHeuristic {
    @Override
    public Double apply(RiskState state) {
        var board = state.getBoard();
        return (double) board.getNrOfTerritoriesOccupiedByPlayer(state.getCurrentPlayer()) / board.getTerritories().size();
    }
}
