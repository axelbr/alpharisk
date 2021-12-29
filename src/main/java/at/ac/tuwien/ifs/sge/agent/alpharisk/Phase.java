package at.ac.tuwien.ifs.sge.agent.alpharisk;

import at.ac.tuwien.ifs.sge.game.ActionRecord;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskBoard;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskTerritory;

import java.util.List;
import java.util.stream.Collectors;

public enum Phase {
    INITIAL_SELECT("INITIAL_SELECT"),
    INITIAL_REINFORCE("INITIAL_REINFORCE"),
    REINFORCE("REINFORCE"),
    ATTACK("ATTACK"),
    OCCUPY("OCCUPY"),
    FORTIFY("FORTIFY");

    private String name;

    Phase(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public Phase update(Risk state) {
        switch (this) {
            case INITIAL_SELECT:
                return updateInitialSelect(state);
            case INITIAL_REINFORCE:
                return updateInitialReinforce(state);
            default:
                return updateDefault(state);
        }
    }

    private Phase updateDefault(Risk state) {
        if (state.getBoard().isReinforcementPhase()) {
            return Phase.REINFORCE;
        } else if (state.getBoard().isAttackPhase()) {
            return Phase.ATTACK;
        } else if (state.getBoard().isOccupyPhase()) {
            return Phase.OCCUPY;
        } else {
            return Phase.FORTIFY;
        }
    }

    private Phase updateInitialSelect(Risk state) {
        boolean notAllSelected = state.getBoard().getTerritories().values().stream()
                .map(RiskTerritory::getOccupantPlayerId)
                .anyMatch(id -> id < 0 || id > state.getNumberOfPlayers());
        if (notAllSelected) {
            return Phase.INITIAL_SELECT;
        } else  {
            return Phase.INITIAL_REINFORCE;
        }
    }

    private Phase updateInitialReinforce(Risk state) {
        boolean isNotInitialReinforce = state.getPossibleActions().stream()
                .anyMatch(action -> action.troops() > 1);
        if (isNotInitialReinforce) {
            return Phase.REINFORCE;
        } else {
            return Phase.INITIAL_REINFORCE;
        }
    }
}
