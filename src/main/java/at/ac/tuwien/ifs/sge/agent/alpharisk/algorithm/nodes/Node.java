package at.ac.tuwien.ifs.sge.agent.alpharisk.algorithm.nodes;

import at.ac.tuwien.ifs.sge.agent.alpharisk.Phase;
import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.util.node.GameNode;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface Node {
    Optional<RiskAction> getAction();
    RiskState getState();
    Set<RiskAction> getPossibleActions();
    double getValue();
    int getPlays();
    void update(double value);
}
