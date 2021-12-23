package at.ac.tuwien.ifs.sge.agent.alpharisk.model;

import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.State;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;

public class ValueTranslator implements Translator<Risk, Double> {

    private final State stateAdapter;

    public ValueTranslator(int playerId) {
        this.stateAdapter = new State(playerId);
    }

    @Override
    public Double processOutput(TranslatorContext ctx, NDList list) throws Exception {
        return (double) list.get("value").getFloat();
    }

    @Override
    public NDList processInput(TranslatorContext ctx, Risk input) throws Exception {
        NDArray state = this.stateAdapter.toVector(input);
        state.setName("state");
        return new NDList(state);
    }
}
