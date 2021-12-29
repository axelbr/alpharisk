package at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.actions;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.index.NDIndex;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.ActionAdapter;
import at.ac.tuwien.ifs.sge.agent.alpharisk.util.MemoryManager;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.HashMap;

public class FortifyAction implements ActionAdapter<RiskAction> {

    private final int numberOfTroopFields;
    private final int numberOfTerritories;
    private final HashMap<Integer, Integer> troops;
    private final NDManager manager = MemoryManager.getManager();

    public FortifyAction(int numberOfTerritories, int numberOfTroopFields) {
        this.numberOfTerritories = numberOfTerritories;
        this.numberOfTroopFields = numberOfTroopFields;
        troops = new HashMap<>();
        for (int i = 0; i < numberOfTroopFields; i++) {
            troops.put(i, 1+i);
        }
    }

    @Override
    public RiskAction toAction(NDArray array) {
        if (array.get(new NDIndex(-2)).getInt() == 1) {
            return RiskAction.endPhase();
        } else {
            int src = (int) array.get(new NDIndex(":" + numberOfTerritories)).argMax().getLong();
            int target = (int) array.get(new NDIndex(numberOfTerritories + ":" + 2 * numberOfTerritories)).argMax().getLong();
            int troopIndex = (int) array.get(new NDIndex(- numberOfTroopFields + ":-2")).argMax().getLong();
            int troops = 1 + troopIndex * 2;
            return RiskAction.fortify(src, target, troops);
        }
    }

    @Override
    public NDArray toArray(RiskAction action) {
        NDArray array = manager.zeros(new Shape(2L * numberOfTerritories + numberOfTroopFields + 2, 1), DataType.INT32);
        if (action.isEndPhase()) {
            array.set(new NDIndex(-2), 1);
        } else {
            int src = action.fortifyingId();
            int target = action.fortifiedId();
            int troops = action.troops();
            int troopIndex = this.troops.values().stream().filter(a -> a == troops).findAny().orElseThrow();
            array.set(new NDIndex(src), 1);
            array.set(new NDIndex(numberOfTerritories + target), 1);
            array.set(new NDIndex(2L * numberOfTerritories + troopIndex), 1);
            array.set(new NDIndex(-1), 1);
        }
        return array;
    }

    @Override
    public boolean isValidAction(RiskAction action) {
        return troops.containsValue(action.troops());
    }

    @Override
    public Shape actionShape() {
        return null;
    }

    @Override
    public int actionSpaceSize() {
        return numberOfTerritories*numberOfTerritories*numberOfTroopFields*2;
    }

    @Override
    public int actionIndex(NDArray array) {
        int src = (int) array.get(new NDIndex(":" + numberOfTerritories)).argMax().getLong();
        int target = (int) array.get(new NDIndex(numberOfTerritories + ":" + 2 * numberOfTerritories)).argMax().getLong();
        int troopIndex = (int) array.get(new NDIndex(2* numberOfTerritories + ":-2")).argMax().getLong();
        if (array.get(-2).getInt() == 1) {
            return 0;
        } else {
            return src * (numberOfTerritories + numberOfTroopFields) + target * numberOfTroopFields + troopIndex;
        }
    }
}
