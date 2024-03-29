package at.ac.tuwien.ifs.sge.agent.alpharisk.domain.states;

import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.ActionValueFunction;
import at.ac.tuwien.ifs.sge.game.risk.board.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class RiskState {
    private Risk risk;
    private Phase phase;

    public void setUtilityFunction(ActionValueFunction utilityFunction) {
        this.utilityFunction = utilityFunction;
    }

    private ActionValueFunction utilityFunction;

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

    public double getUtility() {
        return risk.getUtilityValue(risk.getCurrentPlayer());
    }

    public double getActionUtility(RiskAction action){return utilityFunction.evaluate(this, action);}

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


    public boolean controlsContinent(int continentId, int player) {
        var board = risk.getBoard();
        var occupants = board.getTerritories().values().stream()
                .filter(t -> t.getContinentId() == continentId)
                .map(RiskTerritory::getOccupantPlayerId)
                .collect(Collectors.toList());
        boolean atLeastOneTerritory = occupants.stream().anyMatch(i -> i == player);
        boolean allTerritorriesSameOccupant = occupants.stream().distinct().count() == 1;
        return allTerritorriesSameOccupant && atLeastOneTerritory;
    }

    public List<Integer> getControlledContinents(int player) {
        return getBoard().getContinentIds()
                .stream().filter(c -> controlsContinent(c, player))
                .collect(Collectors.toList());
    }

    public int computeBonus(int player) {
        var board = risk.getBoard();
        int territoryBonus = board.getTerritoriesOccupiedByPlayer(player).size();
        int contintentBonus = board.getContinentIds().stream()
                .filter(c -> controlsContinent(c,player))
                .map(board::getContinentBonus)
                .reduce(0, Integer::sum);
        //int tradeInBonus = board.hasToTradeInCards(player) ? board.getTradeInBonus() : 0;
        return Integer.max(3, territoryBonus / 3) + contintentBonus; //+ tradeInBonus;
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
