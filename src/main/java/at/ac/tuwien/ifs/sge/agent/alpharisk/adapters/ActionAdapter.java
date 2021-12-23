package at.ac.tuwien.ifs.sge.agent.alpharisk.adapters;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.types.Shape;

import java.util.function.Predicate;

public interface ActionAdapter<T> {
    T toAction(NDArray array);
    NDArray toArray(T action);
    Shape actionShape();
    int actionSpaceSize();
    default boolean isValidAction(T action) {
        return true;
    }

    int actionIndex(NDArray array);
}
