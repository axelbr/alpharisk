package at.ac.tuwien.ifs.sge.agent.alpharisk.adapters;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.types.Shape;


public interface StateAdapter<T> {
    NDArray toVector(T item);
    Shape getShape();
}
