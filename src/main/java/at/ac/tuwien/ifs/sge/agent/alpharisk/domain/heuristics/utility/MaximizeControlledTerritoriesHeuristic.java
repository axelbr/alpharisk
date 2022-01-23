package at.ac.tuwien.ifs.sge.agent.alpharisk.domain.heuristics.utility;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.RiskState;

public class MaximizeControlledTerritoriesHeuristic implements StateUtilityHeuristic {

    public double calc(RiskState state, int playerID) {
        var board = state.getBoard();
        return (double) board.getNrOfTerritoriesOccupiedByPlayer(playerID) / board.getTerritories().size();
    }
}
