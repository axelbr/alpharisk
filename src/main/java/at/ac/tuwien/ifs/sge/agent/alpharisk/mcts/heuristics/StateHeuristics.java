package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.heuristics;

public class StateHeuristics {
    public static ValueFunction territoryRatioHeuristic() {
        return s ->  (double) s.getBoard().getNrOfTerritoriesOccupiedByPlayer(s.getCurrentPlayer()) / s.getBoard().getTerritories().size();
    }

    public static ValueFunction continentRatioHeuristic() {
        return s -> {
            var controlledContinents = s.getControlledContinents(s.getCurrentPlayer());
            return (double) controlledContinents.size() / s.getBoard().getContinentIds().size();
        };
    }
}
