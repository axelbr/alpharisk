package at.ac.tuwien.ifs.sge.agent.alpharisk;

import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ai.djl.modality.rl.ActionSpace;
import ai.djl.modality.rl.LruReplayBuffer;
import ai.djl.modality.rl.ReplayBuffer;
import ai.djl.modality.rl.env.RlEnv;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.util.Pair;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskTerritory;

import static at.ac.tuwien.ifs.sge.agent.alpharisk.AlphaRisk.BATCH_SIZE;
import static at.ac.tuwien.ifs.sge.agent.alpharisk.AlphaRisk.REPLAY_BUFFER_SIZE;

public class RiskRlEnvWrapper implements RlEnv {

    public Risk risk;
    private final NDManager manager;
    private final ReplayBuffer replayBuffer;

    private final List<Pair<Integer, Integer>> allEdges = new ArrayList<>();
    private final List<RiskTerritory> territoryList = new ArrayList<>();

    public static int gameStep = 0;
    public static int trainStep = 0;
    private static boolean currentTerminal = false;
    private static float currentReward = 0.2f;
    private String trainState = "observe";

    private List<RiskAction> currentActionSpace = null;

    public RiskRlEnvWrapper(Risk risk) {
        this.risk = risk;
        this.manager = NDManager.newBaseManager();
        this.replayBuffer = new LruReplayBuffer(BATCH_SIZE, REPLAY_BUFFER_SIZE);
        createAllEdges();
        createTerritoryList();
    }

    public RiskRlEnvWrapper() {
        this(new Risk());
    }

    @Override
    public void reset() {
        this.risk = new Risk();
    }

    @Override
    public NDList getObservation() {
        NDArray phase;
        NDArray
            territoryOwners =
            manager.create(territoryList.stream().mapToInt(RiskTerritory::getOccupantPlayerId).toArray()).toType(DataType.FLOAT32, false);
        NDArray territoryTroops = manager.create(territoryList.stream().mapToInt(RiskTerritory::getTroops).toArray()).toType(DataType.FLOAT32, false);
        if (risk.getBoard().isAttackPhase()) {
            phase = manager.create(new float[]{1, 0, 0, 0});
        } else if (risk.getBoard().isFortifyPhase()) {
            phase = manager.create(new float[]{0, 1, 0, 0});
        } else if (risk.getBoard().isOccupyPhase()) {
            phase = manager.create(new float[]{0, 0, 1, 0});
        } else if (risk.getBoard().isReinforcementPhase()) {
            phase = manager.create(new float[]{0, 0, 0, 1});
        } else {
            throw new RuntimeException("Game in unknown phase!");
        }
        return new NDList(territoryOwners.concat(territoryTroops).concat(phase));
    }

    @Override
    public ActionSpace getActionSpace() {
        /*
            final List<RiskAction> initialSelectActions = game.getBoard().getTerritories().keySet().stream().sorted().map(RiskAction::select).collect(Collectors.toList());
            final List<RiskAction> initialReinforceActions = game.getBoard().getTerritories().keySet().stream().sorted().map((Integer id) -> RiskAction.reinforce(id, 1)).collect(Collectors.toList());

            final List<RiskAction> attackActions = new ArrayList<>();
            final List<RiskAction> tradeInActions = new ArrayList<>();
            final List<RiskAction> reinforceActions = new ArrayList<>();
            final List<RiskAction> occupyActions = new ArrayList<>();
            final List<RiskAction> fortifyActions = new ArrayList<>();

             */
        currentActionSpace = new ArrayList<>(risk.getPossibleActions());
        ActionSpace actions = new ActionSpace();
        for (int i = 0; i < currentActionSpace.size(); i++) {
            actions.add(new NDList(i));
        }
        return actions;
    }

    private void createAllEdges() {
        if (allEdges.size() <= 0) {

            for (Integer from_id : risk.getBoard().getTerritories().keySet()) {
                for (Integer to_id : risk.getBoard().neighboringTerritories(from_id)) {
                    this.allEdges.add(new Pair<>(from_id, to_id));
                }
            }

        }
    }

    private void createTerritoryList() {
        territoryList.addAll(risk.getBoard().getTerritories().entrySet().stream().sorted(Map.Entry.comparingByKey())
                                 .map(Map.Entry::getValue).collect(Collectors.toList()));
    }

    @Override
    public Step step(NDList action, boolean training) {
        throw new NotImplementedException();
    }

    @Override
    public Step[] getBatch() {
        throw new NotImplementedException();
    }

    @Override
    public void close() {
        manager.close();
    }
}
