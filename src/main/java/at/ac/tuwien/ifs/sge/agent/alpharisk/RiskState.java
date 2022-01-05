package at.ac.tuwien.ifs.sge.agent.alpharisk;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskBoard;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskTerritory;

public class RiskState {
    private Risk risk;
    private Phase phase;
    private int availableTroops = 0;
    private Set<Integer> reinforcedTerritories = new HashSet<>();

    public RiskState(Risk risk, Phase phase) {
        this.risk = risk;
        this.phase = phase;
        if (phase == Phase.REINFORCE) {
            this.availableTroops = computeBonus(risk.getCurrentPlayer());
        }
    }

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

    public int computeBonus(int player) {
        return computeTerritoryContinentBonus(player) + (risk.getBoard().hasToTradeInCards(player) ? risk.getBoard()
            .getTradeInBonus() : 0);
    }

    public int computeTerritoryContinentBonus(int player) {
        var board = risk.getBoard();
        int territoryBonus = board.getTerritoriesOccupiedByPlayer(player).size();
        int contintentBonus = board.getContinentIds().stream()
            .filter(c -> controlsContinent(c, player))
            .map(board::getContinentBonus)
            .reduce(0, Integer::sum);
        return Integer.max(3, territoryBonus / 3) + contintentBonus;
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

    public int getAvailableTroops() {
        return availableTroops;
    }

    public RiskState apply(RiskAction action) {
        if (phase == Phase.REINFORCE) {
            availableTroops -= action.troops();
        }
        Risk nextState = (Risk) this.risk.doAction(action);
        while (nextState.getCurrentPlayer() < 0) {
            nextState = (Risk) nextState.doAction();
        }
        return new RiskState(nextState, phase.update(nextState));
    }
}
