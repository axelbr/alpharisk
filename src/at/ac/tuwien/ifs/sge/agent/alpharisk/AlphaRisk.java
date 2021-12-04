package at.ac.tuwien.ifs.sge.agent.alpharisk;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import at.ac.tuwien.ifs.sge.agent.AbstractGameAgent;
import at.ac.tuwien.ifs.sge.agent.GameAgent;
import at.ac.tuwien.ifs.sge.agent.alpharisk.config.RiskConfigFactory;
import at.ac.tuwien.ifs.sge.engine.Logger;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskBoard;

public class AlphaRisk extends AbstractGameAgent<Risk, RiskAction> implements GameAgent<Risk, RiskAction> {
    public AlphaRisk(final Logger log) {
        super(0.75, 5L, TimeUnit.SECONDS, log);
    }

    public void setUp(final int numberOfPlayers, final int playerId) {
        super.setUp(numberOfPlayers, playerId);
    }

    public RiskAction computeNextAction(final Risk game, final long computationTime, final TimeUnit timeUnit) {

        List<Integer> actions = new ArrayList<>();
        int
            nextMoveInt =
            Inference.aiDecision(actions, true, "./pretrained", RiskConfigFactory.getRiskInstance());

        super.setTimers(computationTime, timeUnit);
        this.nanosElapsed();
        this.nanosLeft();
        this.shouldStopComputation();
        final RiskBoard board = game.getBoard();
        board.getNrOfTerritoriesOccupiedByPlayer(this.playerId);
        game.getHeuristicValue();
        game.getHeuristicValue(this.playerId);
        final Set<RiskAction> possibleActions = game.getPossibleActions();
        double bestUtilityValue = Double.NEGATIVE_INFINITY;
        double bestHeuristicValue = Double.NEGATIVE_INFINITY;
        RiskAction bestAction = null;
        for (final RiskAction possibleAction : possibleActions) {
            final Risk next = (Risk) game.doAction(possibleAction);
            final double nextUtilityValue = next.getUtilityValue(this.playerId);
            final double nextHeuristicValue = next.getHeuristicValue(this.playerId);
            if (bestUtilityValue <= nextUtilityValue && (bestUtilityValue < nextUtilityValue
                                                         || bestHeuristicValue <= nextHeuristicValue)) {
                bestUtilityValue = nextUtilityValue;
                bestHeuristicValue = nextHeuristicValue;
                bestAction = possibleAction;
            }
        }
        assert bestAction != null;
        assert game.isValidAction(bestAction);
        this.log.debugf("Found best move: %s", bestAction.toString());
        return bestAction;
    }

    public void tearDown() {
    }

    public void destroy() {
    }
}
