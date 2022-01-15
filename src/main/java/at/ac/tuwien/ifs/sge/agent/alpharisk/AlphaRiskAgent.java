package at.ac.tuwien.ifs.sge.agent.alpharisk;

import java.util.concurrent.TimeUnit;

import at.ac.tuwien.ifs.sge.agent.AbstractGameAgent;
import at.ac.tuwien.ifs.sge.agent.GameAgent;
import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskNodeFactory;
import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states.StateFactory;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.algorithms.rave.RaveNode;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.factories.MCTSFactory;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.MonteCarloTreeSearch;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.factories.NodeFactory;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.factories.WrappedNodeFactory;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.visualization.BoardVisualization;
import at.ac.tuwien.ifs.sge.agent.alpharisk.visualization.TreeVisualization;
import at.ac.tuwien.ifs.sge.engine.Logger;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.Util;
import guru.nidi.graphviz.engine.Format;

public class AlphaRiskAgent extends AbstractGameAgent<Risk, RiskAction> implements GameAgent<Risk, RiskAction> {

    private NodeFactory nodeFactory;
    private RiskState.Phase currentPhase;
    private MonteCarloTreeSearch<RiskState, RiskAction> search;
    private Node root;

    public AlphaRiskAgent(final Logger log) {
        super(0.75, 5L, TimeUnit.SECONDS, log);
        search = MCTSFactory.make(MCTSFactory.RAVE);
        NodeFactory.setInstance(new WrappedNodeFactory(new RiskNodeFactory(), search.nodeConstructor()));
        //NodeFactory.setInstance(new WrappedNodeFactory<>(new RiskNodeFactory());
        currentPhase = RiskState.Phase.INITIAL_SELECT;

    }

    public void setUp(final int numberOfPlayers, final int playerId) {
        super.setUp(numberOfPlayers, playerId);
    }

    public RiskAction computeNextAction(final Risk game, final long computationTime, final TimeUnit timeUnit) {
        super.setTimers(computationTime, timeUnit);
        root = updateSearchTree(game);
        int counter = 0;

        if (currentPhase == RiskState.Phase.INITIAL_REINFORCE) {
            return Util.selectRandom(game.getPossibleActions());
        }

        while (!this.shouldStopComputation()) {
            search.runIteration(root);
            logIteration(counter);
            counter++;
        }
        log.info(String.format("Run %d iterations. Expanded nodes: %d.", counter, root.size()));
        logResults();
        var action = search.getBestAction(root);
        return action;
    }

    private void logIteration(int n) {
        if (n % 16 == 0) {
            TreeVisualization.save(root, String.format("../logs/searchtree-%d.dot", n), Format.DOT);
        }
    }

    private void logResults() {
        BoardVisualization.saveBoard(String.format("../logs/current_board.svg"), root.getState());
        for (var child : root.expandedChildren()) {
            log.info(String.format("Action: %s, Value: %.2f, Visits: %d", child.getAction(), child.getValue(), child.getVisits()));
        }
    }


    private Node updateSearchTree(Risk game) {
        currentPhase = currentPhase.update(game);
        RiskState initialState = StateFactory.make(game, currentPhase);
        root = NodeFactory.root(initialState);
        return root;
    }
}
