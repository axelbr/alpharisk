package at.ac.tuwien.ifs.sge.agent.alpharisk.model;

import ai.djl.basicmodelzoo.basic.Mlp;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.AbstractBlock;
import ai.djl.training.ParameterStore;
import ai.djl.util.PairList;

public class PolicyHead extends AbstractBlock {

    private final Mlp network;
    private final Shape outputShape;

    public PolicyHead(Shape embeddingDim, Shape actionSpaceShape) {
        this.outputShape = actionSpaceShape;
        this.network = addChildBlock("network", new Mlp((int) embeddingDim.get(0), (int) actionSpaceShape.get(0), new int[]{256, 256}));
    }

    @Override
    protected NDList forwardInternal(ParameterStore parameterStore, NDList inputs, boolean training, PairList<String, Object> params) {
        NDList current = inputs;
        current = this.network.forward(parameterStore, current, training, params);
        return current;
    }

    @Override
    public Shape[] getOutputShapes(Shape[] inputShapes) {
        return new Shape[]{outputShape};
    }

    @Override
    protected void initializeChildBlocks(NDManager manager, DataType dataType, Shape... inputShapes) {
        this.network.initialize(manager, dataType, inputShapes);
    }
}
