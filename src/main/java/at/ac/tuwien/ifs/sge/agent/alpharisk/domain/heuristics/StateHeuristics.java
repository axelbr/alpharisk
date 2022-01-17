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

        return s -> {
            var board = s.getBoard();
            int territoryBonus = board.getTerritoriesOccupiedByPlayer(s.getCurrentPlayer()).size();
            int contintentBonus = board.getContinentIds().stream()
                    .filter(c -> s.controlsContinent(c,s.getCurrentPlayer()))
                    .map(board::getContinentBonus)
                    .reduce(0, Integer::sum);
            //int tradeInBonus = board.hasToTradeInCards(player) ? board.getTradeInBonus() : 0;
            var bonus = territoryBonus / 3 + contintentBonus;
            var all_bonuses = IntStream.range(0, s.getBoard().getNumberOfPlayers()).map(s::computeBonus).sum();
            return bonus/all_bonuses;
        };
    }

}
