package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.stoppingcriterions;

public interface StoppingCriterion {
    boolean shouldStop();
    void reset();
}
