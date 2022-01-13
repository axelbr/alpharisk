package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.factories;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.heuristics.StateHeuristics;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.MonteCarloTreeSearch;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.ValueFunction;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.algorithms.HeuristicUCTSearch;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import org.apache.commons.configuration2.Configuration;

public class MCTSFactory {

    public static final String HEURISTIC_UCT = "heuristic_uct";

    public static MonteCarloTreeSearch<RiskState, RiskAction> make(String algorithm) {
        if (HEURISTIC_UCT.equals(algorithm)) {
            return makeHeuristicUCTSearch(HeuristicUCTSearch.getDefaultConfiguration(), StateHeuristics.territoryRatioHeuristic());
        } else {
            throw new IllegalArgumentException(algorithm.toString());
        }
    }

    public static MonteCarloTreeSearch<RiskState, RiskAction> makeHeuristicUCTSearch(Configuration configuration, ValueFunction stateHeuristic) {
       return new HeuristicUCTSearch(configuration, stateHeuristic);
    }
}
