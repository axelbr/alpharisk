package at.ac.tuwien.ifs.sge.agent.alpharisk.model;

import ai.djl.basicmodelzoo.basic.Mlp;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.AbstractBlock;
import ai.djl.nn.Block;
import ai.djl.nn.BlockList;
import ai.djl.training.ParameterStore;
import ai.djl.util.Pair;
import ai.djl.util.PairList;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.ActionAdapter;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.actions.AttackAction;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionValueBlock extends AbstractBlock {

    private final Mlp network;
    private final ValueHead valueHead;
    private final BlockList policyHeads;
    private final Shape embeddingDim;

    public ActionValueBlock(Shape inputShape, Map<String, Shape> actionSpaceShapes) {
        this.embeddingDim = new Shape(128,1);
        this.network = addChildBlock("network", new Mlp((int) inputShape.get(0), 128, new int[]{256, 256, 256, 256, 256}));
        this.valueHead = addChildBlock("value", new ValueHead(embeddingDim));
        this.policyHeads = new BlockList();
        for (String name: actionSpaceShapes.keySet()) {
            Block policyHead = addChildBlock(name, new PolicyHead(embeddingDim, actionSpaceShapes.get(name)));
            policyHeads.add(name, policyHead);
        }
    }

    @Override
    protected void initializeChildBlocks(NDManager manager, DataType dataType, Shape... inputShapes) {
        this.network.initialize(manager, dataType, inputShapes);
        this.valueHead.initialize(manager, dataType, embeddingDim);
        this.policyHeads.forEach(pair -> pair.getValue().initialize(manager, dataType, embeddingDim));
    }

    @Override
    protected NDList forwardInternal(ParameterStore parameterStore, NDList inputs, boolean training, PairList<String, Object> params) {
        NDList embedding = network.forward(parameterStore, inputs, training, params);
        NDList outputs = new NDList();
        NDArray value = valueHead.forward(parameterStore, embedding, training, params).head();
        value.setName("value");
        outputs.add(value);
        for (Pair<String, Block> policyHead: policyHeads) {
            NDArray logits = policyHead.getValue().forward(parameterStore, embedding, training, params).head();
            logits.setName(policyHead.getKey());
            outputs.add(logits);
        }
        return outputs;
    }

    @Override
    public Shape[] getOutputShapes(Shape[] inputShapes) {
        Shape[] shapes = new Shape[1 + policyHeads.size()];
        shapes[0] = new Shape(1,1);
        for (int i = 0; i < policyHeads.size(); i++) {
            shapes[i+1] = policyHeads.get(i).getValue().getOutputShapes(null)[0];
        }
        return shapes;
     }
}
