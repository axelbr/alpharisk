package at.ac.tuwien.ifs.sge.agent.alpharisk;

import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

import ai.djl.modality.rl.ActionSpace;
import ai.djl.modality.rl.LruReplayBuffer;
import ai.djl.modality.rl.ReplayBuffer;
import ai.djl.modality.rl.env.RlEnv;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.util.Pair;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;

import static at.ac.tuwien.ifs.sge.agent.alpharisk.AlphaRisk.BATCH_SIZE;
import static at.ac.tuwien.ifs.sge.agent.alpharisk.AlphaRisk.REPLAY_BUFFER_SIZE;

public class RiskRlEnvWrapper implements RlEnv {

    public Risk game;
    private final NDManager manager;
    private final ReplayBuffer replayBuffer;

    public List<Pair<Integer, Integer>> allEdges = new ArrayList<>();

    public static int gameStep = 0;
    public static int trainStep = 0;
    private static boolean currentTerminal = false;
    private static float currentReward = 0.2f;
    private String trainState = "observe";

    public RiskRlEnvWrapper(Risk game) {
        this.game = game;
        this.manager = NDManager.newBaseManager();
        this.replayBuffer = new LruReplayBuffer(BATCH_SIZE, REPLAY_BUFFER_SIZE);
    }

    public RiskRlEnvWrapper() {
        this(new Risk());
    }

    @Override
    public void reset() {
        this.game = new Risk();
    }

    @Override
    public NDList getObservation() {
        throw new NotImplementedException();
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
        throw new NotImplementedException();
        //return null;
    }

    private void createActionSpace(){
        if (allEdges.size() <= 0) {

            this.allEdges = new ArrayList<>();

            for (Integer from_id : game.getBoard().getTerritories().keySet()) {
                for (Integer to_id : game.getBoard().neighboringTerritories(from_id)) {
                    this.allEdges.add(new Pair<>(from_id, to_id));
                }
            }

        }
    }

    @Override
    public Step step(NDList action, boolean training) {
        throw new NotImplementedException();
        //return null;
    }

    @Override
    public Step[] getBatch() {
        throw new NotImplementedException();
        //return new ai.djl.modality.rl.env.RlEnv.Step[0];
    }

    @Override
    public void close() {
        manager.close();
    }
}
