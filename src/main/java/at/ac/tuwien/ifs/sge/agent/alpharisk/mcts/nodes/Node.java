package at.ac.tuwien.ifs.sge.agent.alpharisk.mcts.nodes;

import at.ac.tuwien.ifs.sge.agent.alpharisk.Phase;
import at.ac.tuwien.ifs.sge.game.Game;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.node.GameNode;

public interface Node extends GameNode<RiskAction> {
    int getPlays();
    int getWins();
    Phase getPhase();
    double computeHeuristic(RiskAction action);
    void update(double value);
}
