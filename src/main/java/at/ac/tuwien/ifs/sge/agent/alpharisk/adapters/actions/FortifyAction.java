package at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.actions;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.types.Shape;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.ActionAdapter;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.StateAdapter;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public class FortifyAction implements ActionAdapter<RiskAction> {

    @Override
    public RiskAction toAction(NDArray array) {
        return null;
    }

    @Override
    public NDArray toArray(RiskAction action) {
        return null;
    }

    @Override
    public Shape actionShape() {
        return null;
    }
}
