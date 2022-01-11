package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts;

public interface Policy<S, A> {
    A selectAction(S state);
}
