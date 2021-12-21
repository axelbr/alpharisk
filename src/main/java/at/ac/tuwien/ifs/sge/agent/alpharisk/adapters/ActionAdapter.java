package at.ac.tuwien.ifs.sge.agent.alpharisk.adapters;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.types.Shape;

public interface ActionAdapter<T> {
    T toAction(NDArray array);
    NDArray toArray(T action);
    Shape actionShape();
}
