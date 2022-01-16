package at.ac.tuwien.ifs.sge.agent.alpharisk.domain.heuristics;

import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.ActionValueFunction;

public class ActionHeuristics {

    public static ActionValueFunction borderSecurityThreatHeuristic() {
        return (s, a) -> {
            var board = s.getBoard();
            var territories = board.getTerritories();
            int borderSecurityThreat = board.neighboringEnemyTerritories(a.reinforcedId()).stream()
                    .map(tid -> territories.get(tid).getTroops())
                    .reduce(0, Integer::sum);
            int troops = territories.get(a.reinforcedId()).getTroops();
            return (double) borderSecurityThreat / troops;
        };
    }
}
