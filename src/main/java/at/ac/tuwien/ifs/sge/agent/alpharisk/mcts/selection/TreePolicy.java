package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.selection;

import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.Node;

import java.util.Collection;
import java.util.function.Function;

@FunctionalInterface
public interface TreePolicy extends Function<Collection<? extends Node>, Node> {
}
