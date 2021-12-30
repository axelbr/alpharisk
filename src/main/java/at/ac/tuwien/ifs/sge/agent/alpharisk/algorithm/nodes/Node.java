package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes;

import at.ac.tuwien.ifs.sge.agent.alpharisk.Phase;
import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.node.GameNode;

import java.util.Optional;
import java.util.Set;

public interface Node {
    Optional<RiskAction> getAction();
    RiskState getState();
    double getValue();
    int getPlays();
    void update(double value);
}
