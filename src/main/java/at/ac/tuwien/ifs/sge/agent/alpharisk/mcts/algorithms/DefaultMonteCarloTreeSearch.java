package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.algorithms;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.MonteCarloTreeSearch;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.ValueFunction;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.expansion.ExpandRandomAction;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.expansion.ExpansionStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.Policy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.rollout.RandomRolloutPolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies.treepolicies.GreedyTreePolicy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.simulation.FullPlayoutSimulationStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.simulation.SimulationStrategy;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.NodeStatistics;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.pair.Pair;
import lombok.Setter;

import java.util.List;
import java.util.function.Function;

@Setter
public class DefaultMonteCarloTreeSearch implements MonteCarloTreeSearch<RiskState, RiskAction> {

    private Policy<Node, RiskAction> treePolicy;
    private Policy<Node, RiskAction> actionSelectionPolicy;
    private Policy<RiskState, RiskAction> rolloutPolicy;
    private SimulationStrategy simulationStrategy;
    private ExpansionStrategy expansionStrategy;
    private ValueFunction utilityFunction;

    public DefaultMonteCarloTreeSearch() {
        treePolicy = new GreedyTreePolicy();
        actionSelectionPolicy = new GreedyTreePolicy();
        rolloutPolicy = new RandomRolloutPolicy();
        simulationStrategy = new FullPlayoutSimulationStrategy();
        expansionStrategy = new ExpandRandomAction();
        utilityFunction = RiskState::getUtility;
    }

    @Override
    public Function<Node, Node> nodeConstructor() {
        return n -> n;
    }

    @Override
    public RiskAction getBestAction(Node node) {
        return actionSelectionPolicy.selectAction(node);
    }

    @Override
    public void runIteration(Node root) {
        var node = select(root);
        var expanded = expand(node);
        if (expanded != null) {
            var trajectory = rollout(expanded.getState());
            backup(expanded, trajectory);
        }
    }

    @Override
    public Node select(Node node) {
        var current = node;
        while (current != null && current.isFullyExpanded() && !current.expandedActions().isEmpty()) {
            var action = treePolicy.selectAction(current);
            if (action == null) {
                break;
            }
            current = current.select(action);
        }
        return current;
    }

    @Override
    public Node expand(Node node) {
        if (node != null && !node.isFullyExpanded()) {
            var action = expansionStrategy.expand(node);
            var child = node.expand(action);
            return child;
        } else {
            return null;
        }
    }

    @Override
    public List<Pair<RiskState, RiskAction>> rollout(RiskState state) {
        return simulationStrategy.simulate(state, rolloutPolicy);
    }

    @Override
    public void backup(Node node, List<Pair<RiskState, RiskAction>> playout) {
        var current = node;
        var lastState = playout.get(playout.size() - 1).getA();
        var currentPlayer = node.getState().getCurrentPlayer();
        var currentValue = utilityFunction.evaluate(lastState);
        while (current != null) {
            if (current.getState().getCurrentPlayer() != currentPlayer) {
                currentValue = 1.0 - currentValue;
                currentPlayer = current.getState().getCurrentPlayer();
            }
            NodeStatistics statistics = NodeStatistics.of("value", currentValue);
            current.update(statistics);
            current = current.getParent();
        }
    }
}
