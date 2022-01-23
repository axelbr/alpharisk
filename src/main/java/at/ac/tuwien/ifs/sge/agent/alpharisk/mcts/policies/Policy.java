package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.policies;

public interface Policy<S, A> {
    A selectAction(S state);
}
