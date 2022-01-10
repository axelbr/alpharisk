package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.stoppingcriterions;

public class TimeoutCriterion implements StoppingCriterion {

    private final long startTime;
    private final long timeout;

    public TimeoutCriterion(long timeout, long startTime) {
        this.timeout = timeout;
        this.startTime = startTime;
    }

    @Override
    public boolean shouldStop() {
        return System.nanoTime() - startTime >= timeout || !Thread.currentThread().isAlive() || Thread.currentThread().isInterrupted();
    }

    @Override
    public void reset() {

    }
}
