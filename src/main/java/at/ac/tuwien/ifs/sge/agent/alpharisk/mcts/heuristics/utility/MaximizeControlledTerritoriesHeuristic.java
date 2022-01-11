package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.heuristics.utility;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;

public class MaximizeControlledTerritoriesHeuristic implements StateUtilityHeuristic {

    public double calc(RiskState state, int playerID) {
        var board = state.getBoard();
        return (double) board.getNrOfTerritoriesOccupiedByPlayer(playerID) / board.getTerritories().size();
    }
}
