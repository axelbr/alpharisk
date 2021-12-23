package at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.actions;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.index.NDIndex;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.ActionAdapter;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.StateAdapter;
import at.ac.tuwien.ifs.sge.agent.util.MemoryManager;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskBoard;

public class AttackAction implements ActionAdapter<RiskAction> {

    private final int numberOfTerritories;
    private final NDManager manager;

    public AttackAction(int numberOfTerritories) {
        this.manager = MemoryManager.getManager();
        this.numberOfTerritories = numberOfTerritories;
    }

    @Override
    public NDArray toArray(RiskAction action) {
        // Layout: [0, ... source territories ..., N - 1, N ... target territories ..., 2 * N - 1, 1 Unit, 2 Units, 3 Units, skip, continue]
        NDArray vector = manager.zeros(new Shape(2L * numberOfTerritories + 5, 1), DataType.INT32);
        if (action.isEndPhase()) {
            vector.set(new NDIndex(-2), 1);
        } else {
            vector.set(new NDIndex(action.attackingId()), 1);
            vector.set(new NDIndex(this.numberOfTerritories + action.selected()), 1);
            vector.set(new NDIndex(2L * numberOfTerritories + action.troops() - 1), 1);
            vector.set(new NDIndex(-1), 1);
        }
        return vector;
    }

    @Override
    public RiskAction toAction(NDArray array) {
        if (array.get(new NDIndex(-2)).getInt() == 1) {
            return RiskAction.endPhase();
        } else {
            int src = (int) array.get(new NDIndex(":" + numberOfTerritories)).argMax().getLong();
            int target = (int) array.get(new NDIndex(numberOfTerritories + ":" + 2 * numberOfTerritories)).argMax().getLong();
            int troops = (int) array.get(new NDIndex("-3:")).argMax().getLong() + 1;
            return RiskAction.attack(src, target, troops);
        }
    }
    @Override
    public Shape actionShape() {
        return new Shape(2L * numberOfTerritories + 5, 1);
    }

    @Override
    public int actionSpaceSize() {
        return numberOfTerritories*numberOfTerritories*3 + 1;
    }

    @Override
    public int actionIndex(NDArray array) {
        int src = (int) array.get(new NDIndex(":" + numberOfTerritories)).argMax().getLong();
        int target = (int) array.get(new NDIndex(numberOfTerritories + ":" + 2 * numberOfTerritories)).argMax().getLong();
        int troopIndex = (int) array.get(new NDIndex("-3:")).argMax().getLong();
        if (array.get(-1).getInt() == 1) {
            return 0;
        } else {
            return src * (numberOfTerritories + 3) + target * 3 + troopIndex;
        }
    }
}
