package at.ac.tuwien.ifs.sge.agent.alpharisk.model;

import ai.djl.basicmodelzoo.basic.Mlp;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.AbstractBlock;
import ai.djl.nn.Activation;
import ai.djl.nn.Block;
import ai.djl.training.ParameterStore;
import ai.djl.util.PairList;

public class ValueHead extends AbstractBlock {

    private final Shape embeddingShape;
    private final Block network;

    public ValueHead(Shape embeddingShape) {
        this.embeddingShape = embeddingShape;
        this.network = addChildBlock("mlp", new Mlp((int) embeddingShape.size(0), 1, new int[]{256,256,256}));
    }

    @Override
    protected NDList forwardInternal(ParameterStore parameterStore, NDList inputs, boolean training, PairList<String, Object> params) {
        NDList current = inputs;
        current = this.network.forward(parameterStore, current, training, params);
        current = Activation.tanh(current);
        return current;
    }

    @Override
    public Shape[] getOutputShapes(Shape[] inputShapes) {
        return new Shape[]{new Shape(1)};
    }

    @Override
    protected void initializeChildBlocks(NDManager manager, DataType dataType, Shape... inputShapes) {
        this.network.initialize(manager, dataType, inputShapes);
    }
}
