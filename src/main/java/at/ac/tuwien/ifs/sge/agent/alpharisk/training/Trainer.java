package at.ac.tuwien.ifs.sge.agent.alpharisk.training;

import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.ActionAdapter;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.State;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.actions.AttackAction;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.actions.FortifyAction;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.actions.ReinforceAction;
import at.ac.tuwien.ifs.sge.agent.alpharisk.model.ActionProbabilityTranslator;
import at.ac.tuwien.ifs.sge.agent.alpharisk.model.ActionValueBlock;
import at.ac.tuwien.ifs.sge.agent.alpharisk.model.ValueTranslator;
import at.ac.tuwien.ifs.sge.agent.alpharisk.util.MemoryManager;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import org.javatuples.Pair;

import java.util.HashMap;
import java.util.Map;

public class Trainer {

    public static void main(String[] args) throws Exception {
        Risk risk = new Risk();
        int numberOfTerritories = risk.getBoard().getTerritories().size();
        Map<String, ActionAdapter<RiskAction>> adapters = Map.of(
                "reinforce", new ReinforceAction(numberOfTerritories, 10),
                "attack", new AttackAction(numberOfTerritories),
                "fortify", new FortifyAction(numberOfTerritories, 10)
        );
        Map<String, Shape> actionShapes = new HashMap<>();
        for (Map.Entry<String, ActionAdapter<RiskAction>> entry: adapters.entrySet()) {
            actionShapes.put(entry.getKey(), new Shape(entry.getValue().actionSpaceSize(), 1));
        }

        State stateAdapter = new State(0);

        NDArray state = stateAdapter.toVector(risk);

        ActionValueBlock block = new ActionValueBlock(state.getShape(), actionShapes);

        NDManager manager = MemoryManager.getManager();
        block.initialize(manager, DataType.FLOAT32, state.getShape());

        Model model = Model.newInstance("action_value_model");
        model.setBlock(block);


        Predictor<Risk, Double> predictor = model.newPredictor(new ValueTranslator(0));
        Predictor<Pair<Risk, RiskAction>, Double> probPredictor = model.newPredictor(new ActionProbabilityTranslator("fortify", stateAdapter, adapters.get("fortify")));
        Double value = predictor.predict(risk);
        Double actionProb = probPredictor.predict(Pair.with(risk, RiskAction.fortify(0, 3, 1)));
        System.out.println("ready");
    }
}
