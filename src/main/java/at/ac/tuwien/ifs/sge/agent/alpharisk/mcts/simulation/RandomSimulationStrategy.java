package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.simulation;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.stoppingcriterions.StoppingCriterion;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import at.ac.tuwien.ifs.sge.util.Util;

public class RandomSimulationStrategy implements SimulationStrategy {

    private final StoppingCriterion stoppingCriterion;

    public RandomSimulationStrategy(StoppingCriterion stoppingCriterion) {
        this.stoppingCriterion = stoppingCriterion;
    }

    @Override
    public Double apply(Node node) {
        var initialState = node.getState();
        var state = initialState;
        stoppingCriterion.reset();
        while (!stoppingCriterion.shouldStop() && state.getPhase() != RiskState.Phase.TERMINATED) {
            var action = Util.selectRandom(state.getGame().getPossibleActions());
            state = state.apply(action);
        }
        if (state.getPhase() == RiskState.Phase.TERMINATED) {
            return state.getGame().getUtilityValue(initialState.getCurrentPlayer());
        } else {
            return (double) state.getGame().getBoard().getNrOfTerritoriesOccupiedByPlayer(state.getCurrentPlayer()) / state.getGame().getBoard().getTerritories().size();
        }
    }
}