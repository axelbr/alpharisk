package at.ac.tuwien.ifs.sge.agent.alpharisk.adapters;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.index.NDIndex;
import ai.djl.ndarray.types.Shape;
import at.ac.tuwien.ifs.sge.agent.util.MemoryManager;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskBoard;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskCard;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskTerritory;

public class State implements StateAdapter<Risk> {

    private final NDManager manager = MemoryManager.getManager();
    private final int playerId;

    public State(int playerId) {
        this.playerId = playerId;
    }

    @Override
    public NDArray toVector(Risk risk) {
        RiskBoard board = risk.getBoard();
        return getTerritoryRepresentation(board)
                .concat(getCardRepresentation(board))
                .concat(getPhaseRepresentation(board));
    }

    private NDArray getTerritoryRepresentation(final RiskBoard board) {
        var territories =  board.getTerritories();
        long offset = territories.size();
        NDArray state = manager.zeros(new Shape(2L * territories.size(), 1));

        for (int i = 0; i < territories.size(); i++) {
            RiskTerritory territory = territories.get(i);
            if (territory.getOccupantPlayerId() == this.playerId) {
                state.set(new NDIndex(i), territories.get(i).getTroops());
            } else {
                state.set(new NDIndex(offset + i), territories.get(i).getTroops());
            }
        }
        return state;
    }

    private NDArray getCardRepresentation(final RiskBoard board) {
        NDArray state = manager.zeros(new Shape(5, 1));
        for (RiskCard card: board.getPlayerCards(this.playerId)) {
            state.set(new NDIndex(card.getCardType()), state.get(card.getCardType()).add(1));
        }
        state.set(new NDIndex(4), board.getPlayerCards(1 - this.playerId).size());
        return state;
    }

    private NDArray getPhaseRepresentation(final RiskBoard board) {
        NDArray state = manager.zeros(new Shape(3, 1));
        if (board.isReinforcementPhase()) {
            state.set(new NDIndex(0), 1);
        } else if (board.isAttackPhase() || board.isOccupyPhase()) {
            state.set(new NDIndex(1), 1);
        } else if (board.isFortifyPhase()) {
            state.set(new NDIndex(2), 1);
        }
        return state;

    }

    @Override
    public Shape getShape() {
        return null;
    }
}
