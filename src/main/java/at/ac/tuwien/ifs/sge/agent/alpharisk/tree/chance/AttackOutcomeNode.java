package at.ac.tuwien.ifs.sge.agent.alpharisk.tree.chance;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.NodeFactories;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.decision.DefaultDecisionNode;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import com.google.common.collect.Sets;

import java.util.*;

class Outcome {

    private static final Map<Integer, Map<Integer, List<Outcome>>> table = Map.of(
            1, Map.of(
                    1, List.of(
                            Outcome.create(1, 0, 0.417),
                            Outcome.create(0, 1, 0.583)
                    ),
                    2, List.of(
                            Outcome.create(1, 0, 0.254),
                            Outcome.create(0, 1, 0.746)
                    )
            ),
            2, Map.of(
                    1, List.of(
                            Outcome.create(1, 0, 0.578),
                            Outcome.create(0, 1, 0.422)
                    ),
                    2, List.of(
                            Outcome.create(2, 0, 0.152),
                            Outcome.create(1, 1, 0.475),
                            Outcome.create(0, 2, 0.373)
                    )
            ),
            3, Map.of(
                    1, List.of(
                            Outcome.create(1, 0, 0.659),
                            Outcome.create(0, 1, 0.341)
                    ),
                    2, List.of(
                            Outcome.create(2, 0, 0.259),
                            Outcome.create(1, 1, 0.504),
                            Outcome.create(0, 2, 0.237)
                    )
            )
    );

    public static List<Outcome> getPossibleOutcomes(int attackers, int defenders) {
        return table.get(attackers).get(defenders);
    }

    public static double getProbability(int attackers, int defenders, int attackerLoss, int defenderLoss) {
        return table.get(attackers).get(defenders)
                .stream()
                .filter(o -> o.defenderLoss == defenderLoss && o.attackerLoss == attackerLoss)
                .map(o -> o.probability)
                .findAny()
                .orElse(0.0);
    }

    public Outcome(int defenderLoss, int attackerLoss, double probability) {
        this.defenderLoss = defenderLoss;
        this.attackerLoss = attackerLoss;
        this.probability = probability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Outcome)) return false;
        Outcome outcome = (Outcome) o;
        return defenderLoss == outcome.defenderLoss && attackerLoss == outcome.attackerLoss;
    }

    @Override
    public int hashCode() {
        return Objects.hash(defenderLoss, attackerLoss);
    }

    static Outcome create(int defenderLoss, int attackerLoss, double probability) {
        return new Outcome(defenderLoss, attackerLoss, probability);
    }

    int defenderLoss;
    int attackerLoss;
    double probability;
}

public class AttackOutcomeNode extends ChanceNode {

    private Set<Outcome> possibleOutcomes = new HashSet<>();
    private Set<Outcome> sampledOutcomes = new HashSet<>();

    public AttackOutcomeNode(Node parent, RiskState state, RiskAction action) {
        super(parent, state, action);
        int territoryTroops = this.getState().getGame().getBoard().getTerritoryTroops(getAction().defendingId());
        int defendingTroops = Math.min(2, territoryTroops);
        var outcomes = Outcome.getPossibleOutcomes(getAction().troops(), defendingTroops);
        possibleOutcomes.addAll(outcomes);
    }

    private Outcome computeOutcome(RiskState state, RiskState nextState) {
        var action = getAction();
        int defendingTroops = this.getState().getGame().getBoard().getTerritoryTroops(action.defendingId());
        int defendingLoss = defendingTroops - nextState.getGame().getBoard().getTerritoryTroops(action.defendingId());
        int attackLoss = state.getGame().getBoard().getTerritoryTroops(action.attackingId()) - nextState.getGame().getBoard().getTerritoryTroops(action.attackingId());
        double probability = Outcome.getProbability(action.troops(), getDefendingTroops(action.defendingId()), attackLoss, defendingLoss);
        return Outcome.create(defendingLoss, attackLoss, probability);
    }

    private int getDefendingTroops(int territory) {
        return Math.min(2, getState().getBoard().getTerritoryTroops(territory));
    }

    @Override
    public double getProbability(RiskState nextState) {
        return computeOutcome(getState(), nextState).probability;
    }

    @Override
    protected boolean isAlreadySampled(Node node) {
        var outcome = computeOutcome(getState(), node.getState());
        return sampledOutcomes.contains(outcome);
    }

    @Override
    public Node sampleOutcome() {
        var action = getAction();
        var nextState = getState().apply(action);
        if (nextState.getPhase() == RiskState.Phase.ATTACK) {
            return new AttackOutcomeNode(this, nextState, action);
        } else {
            return new DefaultDecisionNode(this, nextState, action);
        }
    }

    @Override
    public boolean isFullyExpanded() {
        return possibleOutcomes.size() == sampledOutcomes.size();
    }
}
