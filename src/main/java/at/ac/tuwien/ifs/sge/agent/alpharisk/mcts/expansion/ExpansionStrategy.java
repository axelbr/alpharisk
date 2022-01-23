package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.expansion;

import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.Node;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public interface ExpansionStrategy {
    RiskAction expand(Node node);
}
