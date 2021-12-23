package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm;

import ai.djl.basicmodelzoo.basic.Mlp;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.*;
import ai.djl.nn.core.Linear;
import ai.djl.nn.norm.BatchNorm;
import ai.djl.training.ParameterStore;
import ai.djl.util.Pair;
import ai.djl.util.PairList;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.ActionAdapter;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class ActionValueModel extends AbstractBlock {

    private Block network;
    private BlockList policyHeads;
    private Block valueHead;
    private List<ActionAdapter<RiskAction>> adapters;

    public ActionValueModel(Shape stateDim, List<ActionAdapter<RiskAction>> adapters) {
        super();
        int outputDim = 128;
        this.adapters = new ArrayList<>(adapters);
        network = addChildBlock("network", new Mlp((int) stateDim.get(0), outputDim, new int[]{256, 256, 256, 256, 256}));
        valueHead = addChildBlock("value_head", makeValueHead(new Shape(outputDim)));

        policyHeads = new BlockList();
        for (ActionAdapter<RiskAction> adapter: adapters) {
            Block policyHead = addChildBlock(adapter.getClass().getName() + "_policy_head", makeActionHead(new Shape(outputDim), adapter));
            policyHeads.add(adapter.getClass().getName(), policyHead);
        }
    }

    @Override
    protected NDList forwardInternal(ParameterStore parameterStore, NDList inputs, boolean training, PairList<String, Object> params) {
        NDArray states = inputs.get("state");
        NDArray actions = inputs.get("action");
        NDList networkOutputs = network.forward(parameterStore, new NDList(states), training);

        NDList outputs = new NDList();
        NDArray values = valueHead.forward(parameterStore, networkOutputs, training).head();
        values.setName("values");
        outputs.add(values);

        for (Pair<String, Block> entry: policyHeads) {
            Block policy = entry.getValue();
            NDArray actionLogits = policy.forward(parameterStore, networkOutputs, training).head();
            actionLogits.setName(entry.getKey());
            outputs.add(actionLogits);
        }
        return outputs;
    }

    @Override
    protected void initializeChildBlocks(NDManager manager, DataType dataType, Shape... inputShapes) {
        this.network.initialize(manager, dataType, inputShapes[0]);
        Shape embeddingShape = network.getOutputShapes(new Shape[]{inputShapes[0]})[0];
        valueHead.initialize(manager, dataType, embeddingShape);
        for (Block policyHead: policyHeads.values()) {
            policyHead.initialize(manager, dataType, embeddingShape);
        }
    }

    @Override
    public Shape[] getOutputShapes(Shape[] inputShapes) {
        Shape[] shapes =  new Shape[1 + this.adapters.size()];
        shapes[0] = new Shape(1);
        for (int i = 1; i < adapters.size() + 1; i++) {
            shapes[i] = new Shape(adapters.get(i).actionSpaceSize());
        }
        return shapes;
    }

    private Block makeValueHead(Shape inputDim) {
        SequentialBlock head = new SequentialBlock();
        head.add(BatchNorm.builder().build());
        head.add(new Mlp((int) inputDim.get(0), 1, new int[]{256, 128}));
        head.add(Activation.tanhBlock());
        return head;
    }

    private Block makeActionHead(Shape inputDim, ActionAdapter<RiskAction> actionAdapter) {
        SequentialBlock head = new SequentialBlock();
        head.add(BatchNorm.builder().build());
        head.add(new Mlp((int) inputDim.get(0), actionAdapter.actionSpaceSize(), new int[]{256, 256, 128}));
        return head;
    }

    public NDArray getActionProbability(final RiskAction action, final Risk state) {
        return null;
    }

    public NDArray getValue(final Risk state) {
        return null;
    }
}
