package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm;

import ai.djl.ndarray.NDArray;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public class ActionValueModel {

    public NDArray getActionProbability(final RiskAction action, final Risk state) {
        return null;
    }

    public NDArray getValue(final Risk state) {
        return null;
    }
}
