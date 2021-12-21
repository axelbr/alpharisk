package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.models;

import ai.djl.basicmodelzoo.basic.Mlp;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Activation;
import ai.djl.nn.Block;
import ai.djl.nn.SequentialBlock;


public class Models {
    private Models() {}

    public static Block makeSharedBlock(Shape stateDim) {
        return new Mlp((int) stateDim.get(0), 128, new int[]{256, 256, 256, 256, 256});
    }

    public static Block makeValueHead(Block sharedBlock) {
        SequentialBlock head = new SequentialBlock();
        head.add(sharedBlock);
        Mlp valueLayers = new Mlp((int) getOutputShape(sharedBlock).get(0), 2, new int[]{256, 128});
        head.add(valueLayers);
        return head;
    }

    public static Block makePolicyHead(Block sharedBlock) {
        SequentialBlock head = new SequentialBlock();
        head.add(sharedBlock);
        Mlp valueLayers = new Mlp((int) getOutputShape(sharedBlock).get(0), 2, new int[]{256, 128});
        head.add(valueLayers);
        return head;
    }

    private static Shape getOutputShape(Block block) {
        Shape inputShape = block.describeInput().get(0).getValue();
        return block.getOutputShapes(new Shape[]{inputShape})[0];
    }
}
