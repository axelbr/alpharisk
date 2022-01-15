package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.algorithms;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.ValueFunction;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.rollout.RandomRolloutPolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.treepolicies.GreedyTreePolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.treepolicies.HeuristicUCTPolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.simulation.LimitedDepthSimulation;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.pair.Pair;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;

import java.util.List;

public class HeuristicUCTSearch extends DefaultMonteCarloTreeSearch {

    public HeuristicUCTSearch(Configuration config, ValueFunction stateHeuristic) {
        super();
        double explorationConstant = config.getDouble("explorationConstant");
        int rolloutHorizon = config.getInt("rolloutHorizon");
        setActionSelectionPolicy(new GreedyTreePolicy());
        setTreePolicy(new HeuristicUCTPolicy(explorationConstant, stateHeuristic));
        setRolloutPolicy(new RandomRolloutPolicy());
        setSimulationStrategy(new LimitedDepthSimulation(rolloutHorizon));
        setUtilityFunction(s -> Math.random() < stateHeuristic.evaluate(s) ? 1.0 : 0.0);
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
