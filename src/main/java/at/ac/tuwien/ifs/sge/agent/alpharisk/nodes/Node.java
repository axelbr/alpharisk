package at.ac.tuwien.ifs.sge.agent.alpharisk.nodes;

import at.ac.tuwien.ifs.sge.agent.alpharisk.RiskState;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

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
