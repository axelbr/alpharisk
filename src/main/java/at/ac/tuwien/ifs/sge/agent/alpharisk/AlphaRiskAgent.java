package at.ac.tuwien.ifs.sge.agent.alpharisk;

import at.ac.tuwien.ifs.sge.agent.AbstractGameAgent;
import at.ac.tuwien.ifs.sge.agent.GameAgent;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.ActionValueModel;
import at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.AlphaZero;
import at.ac.tuwien.ifs.sge.engine.Logger;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskBoard;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class AlphaRiskAgent extends AbstractGameAgent<Risk, RiskAction> implements GameAgent<Risk, RiskAction> {
    private final AlphaZero algorithm;

    public AlphaRiskAgent(final Logger log, ActionValueModel model) {
        super(0.75, 5L, TimeUnit.SECONDS, log);
        this.algorithm = new AlphaZero(playerId, model);
    }

    public void setUp(final int numberOfPlayers, final int playerId) {
        super.setUp(numberOfPlayers, playerId);
    }

    public RiskAction computeNextAction(final Risk game, final long computationTime, final TimeUnit timeUnit) {
        super.setTimers(computationTime, timeUnit);
        this.nanosElapsed();
        this.nanosLeft();
        this.shouldStopComputation();

        RiskAction bestAction = algorithm.computeAction(game, 10);
        this.log.debugf("Found best move: %s", bestAction.toString());
        return bestAction;
    }

    public void tearDown() {
    }

    public void destroy() {
    }
}
