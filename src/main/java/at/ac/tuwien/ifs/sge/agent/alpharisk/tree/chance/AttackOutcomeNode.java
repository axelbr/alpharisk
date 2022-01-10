package at.ac.tuwien.ifs.sge.agent.alpharisk.tree.chance;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.NodeFactories;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

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
        int territoryTroops = this.getState().getGame().getBoard().getTerritoryTroops(getAction().get().defendingId());
        int defendingTroops = Math.min(2, territoryTroops);
        var outcomes = Outcome.getPossibleOutcomes(getAction().get().troops(), defendingTroops);
        possibleOutcomes.addAll(outcomes);
        sampleAction();
        sampleAction();
        sampleAction();
    }


    @Override
    public Node expand() {
        sampleAction();
        return select();
    }

    private void sampleAction() {
        var action = getAction().get();
        if (sampledOutcomes.size() < possibleOutcomes.size()) {

            RiskState sampledState = getState().apply(this.getAction().get());
            var outcome = computeOutcome(getState(), sampledState);
            if (!sampledOutcomes.contains(outcome)) {
                sampledOutcomes.add(outcome);
                var node = NodeFactories.decisionNodeFactory().makeNode(getParent(), sampledState, action);
                addChild(node);
            }
        }
    }


    private Outcome computeOutcome(RiskState state, RiskState nextState) {
        var action = getAction().get();
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
}
