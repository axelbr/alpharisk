package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.simulation;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.stoppingcriterions.StoppingCriterion;
import at.ac.tuwien.ifs.sge.agent.alpharisk.nodes.Node;
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
        while (!stoppingCriterion.shouldStop() && state.getPhase() != RiskState.Phase.TERMINATED) {
            var action = Util.selectRandom(state.getGame().getPossibleActions());
            state = state.apply(action);
        }
        if (state.getPhase() == RiskState.Phase.TERMINATED) {
            return state.getGame().getUtilityValue(initialState.getCurrentPlayer());
        } else {
            return (double) state.getGame().getBoard().getNrOfTerritoriesOccupiedByPlayer(state.getCurrentPlayer())/state.getGame().getBoard().getTerritories().size();
            /*return Math.random() < state.getGame().getHeuristicValue(initialState.getCurrentPlayer()) / state.getBoard()
                .getTerritories().size() ? 1.0 : 0.0;*/
        }
    }
}
