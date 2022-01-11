package at.ac.tuwien.ifs.sge.agent.alpharisk.domain;

import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.decision.*;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskBoard;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskTerritory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class RiskState {
    private Risk risk;
    private Phase phase;

    public RiskState(Risk risk, Phase phase) {
        this.risk = risk;
        this.phase = phase;
    }

    public abstract Set<RiskAction> getPossibleActions();

    public RiskState(RiskState state) {
        this(new Risk(state.risk), state.phase);
    }

    public Phase getPhase() {
        return phase;
    }

    public Risk getGame() {
        return risk;
    }

    public RiskBoard getBoard() {
        return risk.getBoard();
    }

    public int getCurrentPlayer() {
        return risk.getCurrentPlayer();
    }

    public boolean hasWon() {
        return risk.getBoard().getNrOfTerritoriesOccupiedByPlayer(this.getCurrentPlayer()) > 0;
    }


    private boolean controlsContinent(int continentId, int player) {
        var board = risk.getBoard();
        var occupants = board.getTerritories().values().stream()
                .filter(t -> t.getContinentId() == continentId)
                .map(RiskTerritory::getOccupantPlayerId)
                .collect(Collectors.toList());
        boolean atLeastOneTerritory = occupants.stream().anyMatch(i -> i == player);
        boolean allTerritorriesSameOccupant = occupants.stream().distinct().count() == 1;
        return allTerritorriesSameOccupant && atLeastOneTerritory;
    }

    public RiskState apply(RiskAction action) {
        Risk nextState = (Risk) this.risk.doAction(action);
        while (nextState.getCurrentPlayer() < 0) {
            nextState = (Risk) nextState.doAction();
        }
        var nextPhase = phase.update(nextState);
        return StateFactory.make(nextState, nextPhase);
    }

    public enum Phase {
        INITIAL_SELECT("Initial Select"),
        INITIAL_REINFORCE("Initial Reinforce"),
        REINFORCE("Reinforce"),
        ATTACK("Attack"),
        OCCUPY("Occupy"),
        FORTIFY("Fortify"),
        TRADE_IN("Trade In"),
        TERMINATED("Terminated");
    
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
            if (state.isGameOver()) {
                return Phase.TERMINATED;
            } else if (state.getBoard().hasToTradeInCards(state.getCurrentPlayer())) {
                return Phase.TRADE_IN;
            } else if (state.getBoard().isReinforcementPhase()) {
                return Phase.REINFORCE;
            } else if (state.getBoard().isAttackPhase()) {
                return Phase.ATTACK;
            } else if (state.getBoard().isOccupyPhase()) {
                return Phase.OCCUPY;
            } else if (state.getBoard().isFortifyPhase()) {
                return Phase.FORTIFY;
            }  else {
                throw new IllegalStateException("Illegal game phase");
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
}
