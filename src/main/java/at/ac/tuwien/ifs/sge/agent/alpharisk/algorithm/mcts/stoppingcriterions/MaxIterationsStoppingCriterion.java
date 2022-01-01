package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.mcts.stoppingcriterions;

public class MaxIterationsStoppingCriterion implements StoppingCriterion {
    private int n, initialN;

    public MaxIterationsStoppingCriterion(int n) {
        assert n > 0;
        this.n = n;
        initialN = n;
    }

    @Override
    public void reset() {
        n = initialN;
    }

    @Override
    public boolean shouldStop() {
        return n-- < 1;
    }
}