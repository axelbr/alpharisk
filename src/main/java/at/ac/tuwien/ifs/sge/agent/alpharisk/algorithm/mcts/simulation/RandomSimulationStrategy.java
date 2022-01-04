package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.simulation;

import at.ac.tuwien.ifs.sge.agent.alpharisk.Phase;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.stoppingcriterions.StoppingCriterion;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes.Node;
import at.ac.tuwien.ifs.sge.util.Util;
import at.ac.tuwien.ifs.sge.util.tree.Tree;

public class RandomSimulationStrategy implements SimulationStrategy {

    private final StoppingCriterion stoppingCriterion;

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
            return state.getGame().getUtilityValue(initialState.getCurrentPlayer());
        } else {
            return (double) state.getGame().getBoard().getNrOfTerritoriesOccupiedByPlayer(state.getCurrentPlayer())/state.getGame().getBoard().getTerritories().size();
        }
    }
}
