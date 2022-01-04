package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.stoppingcriterions;

public interface StoppingCriterion {
    boolean shouldStop();
    void reset();
}
