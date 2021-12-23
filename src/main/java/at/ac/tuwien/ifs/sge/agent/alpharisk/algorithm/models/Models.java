package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.models;

import ai.djl.basicmodelzoo.basic.Mlp;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Activation;
import ai.djl.nn.Block;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.norm.BatchNorm;
import at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.ActionAdapter;


public class Models {
    private Models() {}



    public static Block makeSharedBlock(Shape stateDim) {
        return new Mlp((int) stateDim.get(0), 128, new int[]{256, 256, 256, 256, 256});
    }

    public static Block makeValueHead(Shape inputDim) {
        SequentialBlock head = new SequentialBlock();
        head.add(BatchNorm.builder().build());
        head.add(new Mlp((int) inputDim.get(0), 1, new int[]{256, 128}));
        head.add(Activation.tanhBlock());
        return head;
    }

    public static Block makeActionModelHead(Shape inputDim, Shape outputDim) {
        SequentialBlock head = new SequentialBlock();
        head.add(BatchNorm.builder().build());
        head.add(new Mlp((int) inputDim.get(0), (int) outputDim.get(0), new int[]{256, 128}));
        return head;
    }

    private static Shape getOutputShape(Block block) {
        Shape inputShape = block.describeInput().get(0).getValue();
        return block.getOutputShapes(new Shape[]{inputShape})[0];
    }
}
