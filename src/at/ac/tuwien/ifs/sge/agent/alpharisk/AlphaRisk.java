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
        final int nextMoveInt =
            Inference.aiDecision(actions, true, "./pretrained", RiskConfigFactory.getRiskInstance());
        return (RiskAction) game.getPossibleActions().toArray()[nextMoveInt];
    }

    public void tearDown() {
    }

    public void destroy() {
    }
}
