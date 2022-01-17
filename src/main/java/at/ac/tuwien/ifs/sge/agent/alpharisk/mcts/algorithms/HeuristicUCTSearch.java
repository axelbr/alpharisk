package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.algorithms;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.heuristics.StateHeuristics;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.rollout.RandomRolloutPolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.treepolicies.GreedyTreePolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.treepolicies.HeuristicUCTPolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.simulation.LimitedDepthSimulation;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;

public class HeuristicUCTSearch extends DefaultMonteCarloTreeSearch {

    public HeuristicUCTSearch(Configuration config) {
        super();
        setActionSelectionPolicy(new GreedyTreePolicy());
        setTreePolicy(new HeuristicUCTPolicy(config.getDouble("explorationConstant"), wonOrUtility(StateHeuristics.bonusRatioHeuristic())));
        setRolloutPolicy(new RandomRolloutPolicy());
        setSimulationStrategy(new LimitedDepthSimulation(config.getInt("rolloutHorizon")));
        setUtilityFunction(sample(wonOrUtility(StateHeuristics.bonusRatioHeuristic())));
    }

    public static Configuration getDefaultConfiguration() {
        var config = new BaseConfiguration();
        config.setProperty("explorationConstant", 0.5);
        config.setProperty("rolloutHorizon", 32);
        return config;
    }
}
