package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.algorithms;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.MonteCarloTreeSearch;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.ValueFunction;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.expansion.ExpandRandomAction;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.rollout.RandomRolloutPolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.treepolicies.GreedyTreePolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.treepolicies.HeuristicUCTPolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.simulation.LimitedDepthSimulation;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;

public class HeuristicUCTSearch extends DefaultMonteCarloTreeSearch {

    public HeuristicUCTSearch(Configuration config, ValueFunction stateHeuristic) {
        super();
        double explorationConstant = config.getDouble("explorationConstant");
        int rolloutHorizon = config.getInt("rolloutHorizon");
        setActionSelectionPolicy(new GreedyTreePolicy());
        setTreePolicy(new HeuristicUCTPolicy(explorationConstant, stateHeuristic));
        setRolloutPolicy(new RandomRolloutPolicy());
        setSimulationStrategy(new LimitedDepthSimulation(rolloutHorizon, stateHeuristic));
    }

    public HeuristicUCTSearch(ValueFunction valueFunction) {
        this(getDefaultConfiguration(), valueFunction);
    }

    public static Configuration getDefaultConfiguration() {
        var config = new BaseConfiguration();
        config.setProperty("explorationConstant", 0.5);
        config.setProperty("rolloutHorizon", 32);
        return config;
    }
}
