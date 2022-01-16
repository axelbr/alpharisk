package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.factories;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.heuristics.StateHeuristics;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.MonteCarloTreeSearch;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.ValueFunction;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.algorithms.HeuristicUCTSearch;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.algorithms.rave.RapidActionValueEstimationSearch;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import org.apache.commons.configuration2.Configuration;

public class MCTSFactory {

    public static final String HEURISTIC_UCT = "heuristic_uct";
    public static final String RAVE = "rave";

    public static MonteCarloTreeSearch<RiskState, RiskAction> make(String algorithm) {
        if (HEURISTIC_UCT.equals(algorithm)) {
            return makeHeuristicUCTSearch(HeuristicUCTSearch.getDefaultConfiguration());
        } if (RAVE.equals(algorithm)) {
            return makeRaveSearch(RapidActionValueEstimationSearch.getDefaultConfiguration());
        } else {
            throw new IllegalArgumentException(algorithm.toString());
        }
    }

    public static MonteCarloTreeSearch<RiskState, RiskAction> makeHeuristicUCTSearch(Configuration configuration) {
       return new HeuristicUCTSearch(configuration);
    }

    public static MonteCarloTreeSearch<RiskState, RiskAction> makeRaveSearch(Configuration configuration) {
        return new RapidActionValueEstimationSearch(configuration);
    }
}
