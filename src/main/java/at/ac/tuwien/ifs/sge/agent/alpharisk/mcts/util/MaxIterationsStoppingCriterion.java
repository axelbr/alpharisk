package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.util;

import at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.StoppingCriterion;

public class MaxIterationsStoppingCriterion implements StoppingCriterion {
    private int n;

    public MaxIterationsStoppingCriterion(int n) {
        assert n > 0;
        this.n = n;
    }

    @Override
    public boolean shouldStop() {
        return n-- < 1;
    }
}
