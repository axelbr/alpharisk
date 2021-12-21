package at.ac.tuwien.ifs.sge.agent.alpharisk.training;

import ai.djl.Model;
import at.ac.tuwien.ifs.sge.agent.alpharisk.AlphaRiskAgent;
import at.ac.tuwien.ifs.sge.engine.game.Match;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.game.risk.configuration.RiskConfiguration;

public class Trainer {

    public static void main(String[] args) {
        Risk risk = new Risk("./environment/boards/risk_simple_3.yaml", 2);
    }
}
