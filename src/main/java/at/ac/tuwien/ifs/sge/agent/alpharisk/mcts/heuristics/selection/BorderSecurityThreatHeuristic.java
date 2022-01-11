package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.heuristics.selection;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BorderSecurityThreatHeuristic implements ActionSelectionHeuristic {
    @Override
    public RiskAction apply(RiskState state) {
        List<Pair<Integer, Double>> borderSecurityRatio = new ArrayList<>();
        var board = state.getBoard();
        var territories = board.getTerritories();
        double sum = 0;
        for (var territoryId: board.getTerritoriesOccupiedByPlayer(state.getCurrentPlayer())) {
            int borderSecurityThreat = board.neighboringEnemyTerritories(territoryId).stream()
                    .map(tid -> territories.get(tid).getTroops())
                    .reduce(0, Integer::sum);
            int troops = territories.get(territoryId).getTroops();
            double borderSecurityThreatRatio = (double) borderSecurityThreat / troops;
            borderSecurityRatio.add(Pair.create(territoryId, borderSecurityThreatRatio));
            sum += borderSecurityThreatRatio;
        }
        var highRiskTerritory = borderSecurityRatio.stream().max(Comparator.comparingDouble(Pair::getValue)).get();
        double normalizedThreat = highRiskTerritory.getValue() / sum;
        int troops = 0; // (int) (normalizedThreat * state.getAvailableTroops());
        var action = RiskAction.reinforce(highRiskTerritory.getKey(), Math.max(troops, 1));
        return action;
    }
}
