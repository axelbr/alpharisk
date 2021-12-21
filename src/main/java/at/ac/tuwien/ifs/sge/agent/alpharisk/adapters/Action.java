package at.ac.tuwien.ifs.sge.agent.alpharisk.adapters;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.types.Shape;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.ActionAdapter;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.StateAdapter;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.actions.AttackAction;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.actions.FortifyAction;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.actions.ReinforceAction;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public class Action implements ActionAdapter<RiskAction> {

    private final ReinforceAction reinforceAction;
    private final AttackAction attackAction;
    private final FortifyAction fortifyAction;

    public Action(ReinforceAction reinforceAction, AttackAction attackAction, FortifyAction fortifyAction) {
        this.reinforceAction = reinforceAction;
        this.attackAction = attackAction;
        this.fortifyAction = fortifyAction;
    }

    @Override
    public RiskAction toAction(NDArray array) {
        return null;
    }

    @Override
    public NDArray toArray(RiskAction action) {
        return reinforceAction.toArray(action)
                .concat(attackAction.toArray(action))
                .concat(fortifyAction.toArray(action));
    }



    @Override
    public Shape actionShape() {
        return attackAction.actionShape()
                .addAll(reinforceAction.actionShape())
                .addAll(fortifyAction.actionShape());
    }
}
