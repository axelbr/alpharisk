package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts;

@FunctionalInterface
public interface StoppingCriterion {
    boolean shouldStop();
}
