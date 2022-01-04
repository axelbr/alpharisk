package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.simulation;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.ifs.sge.agent.alpharisk.Phase;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.heuristics.utility.BonusRatioHeuristic;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.heuristics.utility.MaximizeControlledTerritoriesHeuristic;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.heuristics.utility.StateUtilityHeuristic;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.stoppingcriterions.StoppingCriterion;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes.Node;
import at.ac.tuwien.ifs.sge.util.Util;
import at.ac.tuwien.ifs.sge.util.tree.Tree;

public class RandomSimulationStrategy implements SimulationStrategy {

    private final StoppingCriterion stoppingCriterion;
    private final StateUtilityHeuristic territoryHeursitic = new MaximizeControlledTerritoriesHeuristic();
    private final StateUtilityHeuristic bonusHeuristic = new BonusRatioHeuristic();

    public RandomSimulationStrategy(StoppingCriterion stoppingCriterion) {
        this.stoppingCriterion = stoppingCriterion;
    }

    @Override
    public Double apply(Tree<Node> tree) {
        var initialState = tree.getNode().getState();
        var state = initialState;
        stoppingCriterion.reset();
        while (!stoppingCriterion.shouldStop() && state.getPhase() != Phase.TERMINATED) {
            var action = Util.selectRandom(state.getGame().getPossibleActions());
            state = state.apply(action);
        }
        if (state.getPhase() == Phase.TERMINATED) {
            System.out.println("Simulation Terminated!");
            return state.getGame().getUtilityValue(initialState.getCurrentPlayer());
        } else {
            var
                territoryRatio = territoryHeursitic.calc(state, initialState.getCurrentPlayer());

            var
                bonus = bonusHeuristic.calc(state, initialState.getCurrentPlayer());

            /*return
                Math.random() <= state.getGame().getHeuristicValue(initialState.getCurrentPlayer()) / state.getBoard()
                    .getTerritories().size() ? 1.0 : 0.0;*/
            return .5*territoryRatio + .5*bonus;
        }
    }
}
