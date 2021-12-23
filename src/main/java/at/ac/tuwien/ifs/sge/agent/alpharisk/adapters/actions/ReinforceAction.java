package at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.actions;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.index.NDIndex;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.ActionAdapter;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.StateAdapter;
import at.ac.tuwien.ifs.sge.agent.util.MemoryManager;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReinforceAction implements ActionAdapter<RiskAction> {

    private final ArrayList<Integer> troopMapping;
    private int numberOfTerritories;
    private final NDManager manager = MemoryManager.getManager();

    public ReinforceAction(int numberOfTerritories, int numberOfTroopFields) {
        this.numberOfTerritories = numberOfTerritories;
        troopMapping = new ArrayList<>();
        for (int i = 0; i < numberOfTroopFields; i++) {
            troopMapping.add(fibonacci(i+2));
        }
    }

    private int fibonacci(int x) {
        if (x <= 2) return 1;
        return fibonacci(x-1) + fibonacci(x-2);
    }

    @Override
    public RiskAction toAction(NDArray array) {
        int src = (int) array.get(new NDIndex(":" + numberOfTerritories)).argMax().getLong();
        int troopIdx = (int) array.get(new NDIndex(numberOfTerritories + ":")).argMax().getLong();
        return RiskAction.reinforce(src, troopMapping.get(troopIdx));
    }

    @Override
    public NDArray toArray(RiskAction action) {
        NDArray array = manager.zeros(new Shape(numberOfTerritories + troopMapping.size(), 1), DataType.INT32);
        array.set(new NDIndex(action.reinforcedId()), 1);
        int index = troopMapping.indexOf(action.troops());
        array.set(new NDIndex(numberOfTerritories + index), 1);
        return array;
    }

    @Override
    public Shape actionShape() {
        return null;
    }

    @Override
    public int actionSpaceSize() {
        return numberOfTerritories * troopMapping.size();
    }

    @Override
    public boolean isValidAction(RiskAction action) {
        return ActionAdapter.super.isValidAction(action);
    }

    @Override
    public int actionIndex(NDArray array) {
        int troopIndex = array.get(new NDIndex(-troopMapping.size() + ":")).argMax().getInt();
        int territoryIndex = array.get(new NDIndex(":" + numberOfTerritories)).argMax().getInt();
        return territoryIndex * troopMapping.size() + troopIndex;
    }
}
