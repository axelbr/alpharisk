package at.ac.tuwien.ifs.sge.agent.alpharisk.model;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDArrays;
import ai.djl.ndarray.NDList;
import ai.djl.nn.Activation;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.ActionAdapter;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.State;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.actions.AttackAction;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.actions.FortifyAction;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.actions.ReinforceAction;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import org.javatuples.Pair;

public class ActionProbabilityTranslator implements Translator<Pair<Risk, RiskAction>, Double> {

    private final ActionAdapter<RiskAction> actionAdapter;
    private final State stateAdapter;
    private RiskAction action;
    private final String outputKey;

    public ActionProbabilityTranslator(String actionName, State stateAdapter, ActionAdapter<RiskAction> actionAdapter) {
        this.actionAdapter = actionAdapter;
        this.stateAdapter = stateAdapter;
        if (actionAdapter instanceof AttackAction) {
            outputKey = "attack";
        } else if (actionAdapter instanceof FortifyAction) {
            outputKey = "fortify";
        } else if (actionAdapter instanceof ReinforceAction) {
            outputKey = "reinforce";
        } else {
            throw new IllegalArgumentException("no such action adapter supported");
        }
    }

    @Override
    public Double processOutput(TranslatorContext ctx, NDList list) throws Exception {
        NDArray action = actionAdapter.toArray(this.action);
        NDArray logits = list.get(outputKey);
        int index = actionAdapter.actionIndex(action);
        NDArray probabilities = logits.exp().divi(logits.exp().sum());
        double prob = (double) probabilities.get(index).getFloat();
        return prob;
    }

    @Override
    public NDList processInput(TranslatorContext ctx, Pair<Risk, RiskAction> input) throws Exception {
        this.action = input.getValue1();
        NDArray state = stateAdapter.toVector(input.getValue0());
        state.setName("state");
        return new NDList(state);
    }
}
