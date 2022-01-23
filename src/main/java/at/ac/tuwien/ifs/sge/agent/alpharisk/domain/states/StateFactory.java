package at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states;

import at.ac.tuwien.ifs.sge.game.risk.board.Risk;

public class StateFactory {
    public static RiskState make(Risk game, RiskState.Phase phase) {
        switch (phase) {
            case INITIAL_SELECT:
                return new DefaultState(game, phase);
            case INITIAL_REINFORCE:
                return new InitialReinforceState(game, phase);
            case TRADE_IN:
                return new DefaultState(game, phase);
            case REINFORCE:
                return new ReinforceState(game, phase);
            case FORTIFY:
                return new FortifyState(game, phase);
            case OCCUPY:
                return new OccupyState(game, phase);
            case ATTACK:
                return new AttackState(game, phase);
            case TERMINATED:
                return new TerminalState(game, phase);
            default:
                throw new IllegalArgumentException(phase.toString());
        }
    }
}
