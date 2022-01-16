package at.ac.tuwien.ifs.sge.agent.alpharisk.domain.heuristics;

import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.ValueFunction;

import java.util.stream.IntStream;

public class StateHeuristics {
    public static ValueFunction territoryRatioHeuristic() {
        return s -> (double) s.getBoard().getNrOfTerritoriesOccupiedByPlayer(s.getCurrentPlayer()) /
                    s.getBoard().getTerritories().size();
    }

    public static ValueFunction continentRatioHeuristic() {
        return s -> {
            var controlledContinents = s.getControlledContinents(s.getCurrentPlayer());
            return (double) controlledContinents.size() / s.getBoard().getContinentIds().size();
        };
    }

    public static ValueFunction bonusRatioHeuristic() {
        return s -> (double) s.computeBonus(s.getCurrentPlayer()) /
                    IntStream.range(0, s.getBoard().getNumberOfPlayers()).map(s::computeBonus).sum();
    }

}
