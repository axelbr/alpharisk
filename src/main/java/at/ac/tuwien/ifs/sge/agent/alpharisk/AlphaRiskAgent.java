package at.ac.tuwien.ifs.sge.agent.alpharisk;

import at.ac.tuwien.ifs.sge.agent.AbstractGameAgent;
import at.ac.tuwien.ifs.sge.agent.GameAgent;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.MonteCarloTreeSearch;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes.NodeFactory;
import at.ac.tuwien.ifs.sge.engine.Logger;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.*;

public class AlphaRiskAgent extends AbstractGameAgent<Risk, RiskAction> implements GameAgent<Risk, RiskAction>
{
    private Phase currentPhase;
    private ExecutorService executor = Executors.newCachedThreadPool();
    MonteCarloTreeSearch search = new MonteCarloTreeSearch(new MonteCarloTreeSearch.HyperParameters());


    public AlphaRiskAgent(final Logger log) {
        super(0.75, 5L, TimeUnit.SECONDS, log);
        currentPhase = Phase.INITIAL_SELECT;
    }
    
    public void setUp(final int numberOfPlayers, final int playerId) {
        super.setUp(numberOfPlayers, playerId);
    }
    
    public RiskAction computeNextAction(final Risk game, final long computationTime, final TimeUnit timeUnit) {
        super.setTimers(computationTime, timeUnit);
        currentPhase = currentPhase.update(game);
        RiskState initialState = new RiskState(game, currentPhase);
        ExecutorService service = Executors.newCachedThreadPool();
        Future<RiskAction> actionFuture = service.submit(() -> search.computeAction(initialState));
        RiskAction action;
        try {
            action = actionFuture.get(nanosLeft(), TimeUnit.NANOSECONDS);
        } catch (TimeoutException e) {
            action = search.getCurrentBestAction();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e.getCause());
        }
        return action;
    }
}
