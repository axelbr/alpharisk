package at.ac.tuwien.ifs.sge.agent.alpharisk.adapters.actions;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.index.NDIndex;
import ai.djl.ndarray.types.Shape;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;



public class AttackActionTest {

    private Risk risk;
    private NDManager ndManager;

    @BeforeEach
    public void setup() {
        risk = new Risk();
        ndManager = NDManager.newBaseManager();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void testValidRiskAttackToVector(int troops) {
        AttackAction adapter = new AttackAction(risk.getBoard().getTerritories().size());
        int numberOfTerritories = risk.getBoard().getTerritories().size();
        RiskAction action = RiskAction.attack(0, numberOfTerritories-1, troops);
        NDArray result = adapter.toArray(action);
        NDArray expected = ndManager.zeros(new Shape(2L * numberOfTerritories+ 3L, 1));
        for (int index: List.of(0, 2 * numberOfTerritories - 1, 2 * numberOfTerritories+troops - 1)) {
            expected.set(new NDIndex(index), 1);
        }
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void testValidVectorToRiskAttack(int troops) {
        AttackAction adapter = new AttackAction(risk.getBoard().getTerritories().size());
        int numberOfTerritories = risk.getBoard().getTerritories().size();
        NDArray actionVector = ndManager.zeros(new Shape(2L * numberOfTerritories+ 3L, 1));
        for (int index: List.of(0, 2 * numberOfTerritories - 1, 2 * numberOfTerritories+troops - 1)) {
            actionVector.set(new NDIndex(index), 1);
        }
        RiskAction action = adapter.toAction(actionVector);
        RiskAction expected = RiskAction.attack(0, numberOfTerritories-1, troops);
        assertEquals(expected, action);
    }
}
