package at.ac.tuwien.ifs.sge.agent.alpharisk;

import at.ac.tuwien.ifs.sge.agent.AbstractGameAgent;
import at.ac.tuwien.ifs.sge.agent.GameAgent;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.MonteCarloTreeSearch;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.nodes.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.nodes.NodeFactory;
import at.ac.tuwien.ifs.sge.engine.Logger;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.concurrent.*;

public class AlphaRiskAgent extends AbstractGameAgent<Risk, RiskAction> implements GameAgent<Risk, RiskAction>
{
    private Phase currentPhase;
    private ExecutorService executor = Executors.newCachedThreadPool();

    public AlphaRiskAgent(final Logger log) {
        super(0.75, 5L, TimeUnit.SECONDS, log);
        currentPhase = Phase.INITIAL_SELECT;
    }
    
    public void setUp(final int numberOfPlayers, final int playerId) {
        super.setUp(numberOfPlayers, playerId);
    }
    
    public RiskAction computeNextAction(final Risk game, final long computationTime, final TimeUnit timeUnit) {
        currentPhase = currentPhase.update(game);
        Node root = NodeFactory.makeNode(game, currentPhase);
        MonteCarloTreeSearch search = new MonteCarloTreeSearch(root, currentPhase);
        var ignore = executor.submit(search::runSearch);
        try {
            ignore.get(computationTime, timeUnit);
        } catch (ExecutionException | InterruptedException | TimeoutException ignored) {
        }
        RiskAction bestAction = search.getBestAction();
        this.log.debugf("Found best move: %s", bestAction.toString());
        return bestAction;
    }
    
    public void tearDown() {
    }
    
    public void destroy() {
    }
}
