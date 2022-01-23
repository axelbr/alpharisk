package at.ac.tuwien.ifs.sge.agent.alpharisk.domain.heuristics;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.ValueFunction;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

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

    private static double computeUnscaledBonus(RiskState s, int playerID){
        var board = s.getBoard();
        int territoryBonus = board.getTerritoriesOccupiedByPlayer(playerID).size();
        double contintentBonus = board.getContinentIds().stream()
                .filter(c -> s.controlsContinent(c,playerID))
                .mapToDouble(board::getContinentBonus)
                .reduce(0, Double::sum);
        //int tradeInBonus = board.hasToTradeInCards(player) ? board.getTradeInBonus() : 0;
        return (double) territoryBonus + contintentBonus;
    }

    public static ValueFunction bonusRatioHeuristic() {

        return s -> {
            var all_bonuses = IntStream.range(0, s.getBoard().getNumberOfPlayers()).mapToDouble(id -> computeUnscaledBonus(s,id)).sum();
            return computeUnscaledBonus(s, s.getCurrentPlayer()) / all_bonuses;
        };
    }

}
