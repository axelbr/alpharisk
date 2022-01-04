package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes;

import at.ac.tuwien.ifs.sge.agent.alpharisk.Phase;
import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.game.Game;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.Util;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractNode implements Node {

    private final RiskAction action;
    private int plays;
    private double value;
    private RiskState state;
    private Set<RiskAction> possibleActions;

    public AbstractNode(final RiskState state, RiskAction previousAction) {
        this.state = new RiskState(state);
        this.action = previousAction;
        this.possibleActions = state.getGame().getPossibleActions();
    }

    public AbstractNode(final RiskState state) {
        this(state, null);
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public Optional<RiskAction> getAction() {
        if (action == null) {
            return Optional.empty();
        }
        return Optional.of(action);
    }

    @Override
    public int getPlays() {
        return plays;
    }

    @Override
    public void update(double value) {
        this.plays += 1;
        this.value += value;
    }

    @Override
    public RiskState getState() {
        return state;
    }

    @Override
    public Set<RiskAction> getPossibleActions() {
        return possibleActions;
    }
}
