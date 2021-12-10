package at.ac.tuwien.ifs.sge.agent.alpharisk;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import ai.djl.util.Pair;
import at.ac.tuwien.ifs.sge.agent.AbstractGameAgent;
import at.ac.tuwien.ifs.sge.agent.GameAgent;
import at.ac.tuwien.ifs.sge.agent.alpharisk.config.RiskConfigFactory;
import at.ac.tuwien.ifs.sge.engine.Logger;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public class AlphaRisk extends AbstractGameAgent<Risk, RiskAction> implements GameAgent<Risk, RiskAction> {

    public List<Pair<Integer, Integer>> allEdges = new ArrayList<>();

    public AlphaRisk(final Logger log) {
        super(0.75, 5L, TimeUnit.SECONDS, log);
    }

    public void setUp(final int numberOfPlayers, final int playerId) {
        super.setUp(numberOfPlayers, playerId);
        java.lang.Thread.currentThread().setContextClassLoader(
                java.lang.ClassLoader.getSystemClassLoader()
        );
    }

    public RiskAction computeNextAction(final Risk game, final long computationTime, final TimeUnit timeUnit) {
        if (allEdges.size() <= 0) {

            this.allEdges = new ArrayList<>();

            for (Integer from_id : game.getBoard().getTerritories().keySet()) {
                for (Integer to_id : game.getBoard().neighboringTerritories(from_id)) {
                    this.allEdges.add(new Pair<>(from_id, to_id));
                }
            }

            /*
            final List<RiskAction> initialSelectActions = game.getBoard().getTerritories().keySet().stream().sorted().map(RiskAction::select).collect(Collectors.toList());
            final List<RiskAction> initialReinforceActions = game.getBoard().getTerritories().keySet().stream().sorted().map((Integer id) -> RiskAction.reinforce(id, 1)).collect(Collectors.toList());

            final List<RiskAction> attackActions = new ArrayList<>();
            final List<RiskAction> tradeInActions = new ArrayList<>();
            final List<RiskAction> reinforceActions = new ArrayList<>();
            final List<RiskAction> occupyActions = new ArrayList<>();
            final List<RiskAction> fortifyActions = new ArrayList<>();

             */

        }
        List<Integer> actions = new ArrayList<>();
        final int nextMoveInt =
                Inference.aiDecision(actions, true, "./pretrained", RiskConfigFactory.getRiskInstance());
        RiskAction bestAction = (RiskAction) game.getPossibleActions().toArray()[nextMoveInt];

        assert game.isValidAction(bestAction);
        this.log.debugf("Found best move: %s", bestAction.toString());
        return bestAction;
    }

    public void tearDown() {
    }

    public void destroy() {
    }
}
