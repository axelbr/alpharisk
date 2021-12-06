import at.ac.tuwien.ifs.sge.agent.alpharisk.AlphaRisk;
import at.ac.tuwien.ifs.sge.game.Game;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

import java.util.concurrent.TimeUnit;

public class AlphaRiskTest {

    public static void main(String[] args) {

        Risk exampleGame = new Risk();
        AlphaRisk agent = new AlphaRisk(null);
        // Bring game and agent to the required state
        RiskAction action = agent.computeNextAction(exampleGame, 30, TimeUnit.SECONDS);
        Risk next = (Risk) exampleGame.doAction(action);
            //Test if agent behaves as expected
    }
}
