package at.ac.tuwien.ifs.sge.agent.alpharisk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.modality.rl.agent.QAgent;
import ai.djl.modality.rl.agent.RlAgent;
import ai.djl.modality.rl.env.RlEnv;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Activation;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.core.Linear;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.Trainer;
import ai.djl.training.evaluator.Accuracy;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.Loss;
import ai.djl.training.optimizer.Adam;
import ai.djl.training.tracker.Tracker;
import at.ac.tuwien.ifs.sge.agent.AbstractGameAgent;
import at.ac.tuwien.ifs.sge.agent.GameAgent;
import at.ac.tuwien.ifs.sge.engine.Logger;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;
import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;

public class AlphaRisk extends AbstractGameAgent<Risk, RiskAction> implements GameAgent<Risk, RiskAction> {

    public static final String MODEL_PATH = "./risk_model";
    public static final int BATCH_SIZE = 32;
    public static final int OBSERVE = 1000; // gameSteps to observe before training
    public static final int EXPLORE = 3000000; // frames over which to anneal epsilon
    public static final int SAVE_EVERY_STEPS = 100000; // save model every 100,000 step
    public static final int REPLAY_BUFFER_SIZE = 50000; // number of previous transitions to remember
    public static final float REWARD_DISCOUNT = 0.9f; // decay rate of past observations
    public static final float INITIAL_EPSILON = 0.01f;
    public static final float FINAL_EPSILON = 0.0001f;

    public final RlAgent agent;
    public final Model model;
    public final Trainer trainer;
    public boolean training;
    private boolean isInitialized = false;

    public Logger getLogger() {
        return this.log;
    }

    public AlphaRisk(final Logger log) {
        this(log, false);
    }

    public AlphaRisk(Logger log, boolean training) {
        super(0.75, 5L, TimeUnit.SECONDS, log);
        this.training = training;
        model = Model.newInstance("QNetwork");
        trainer = new Trainer(model, setupTrainingConfig());
        agent = new QAgent(trainer, REWARD_DISCOUNT);
    }

    public AlphaRisk(boolean training) {
        this(new Logger(0, "[sge ", "",
                        "trace]: ", System.out, "",
                        "debug]: ", System.out, "",
                        "info]: ", System.out, "",
                        "warn]: ", System.err, "",
                        "error]: ", System.err, ""), training);
    }

    private void initialize(Shape[] input_shape, int num_actions, boolean load, Path from){
        if (isInitialized) return;

        model.setBlock(getBlock(num_actions));
        if (load && Files.exists(from)) {
            try {
                model.load(from);
            } catch (IOException | MalformedModelException e) {
                e.printStackTrace();
            }
        }

        trainer.initialize(input_shape);
        isInitialized = true;
    }

    private static SequentialBlock getBlock(int num_actions) {
        return new SequentialBlock()
            .add(Linear.builder()
                     .setUnits(1024)
                     .build())
            .add(Activation::relu)

            .add(Linear.builder()
                     .setUnits(1024)
                     .build())
            .add(Activation::relu)

            .add(Linear.builder()
                     .setUnits(1024)
                     .build())
            .add(Activation::relu)

            .add(Linear
                     .builder()
                     .setUnits(512).build())
            .add(Activation::relu)

            .add(Linear
                     .builder()
                     .setUnits(num_actions).build());
    }

    public void setUp(final int numberOfPlayers, final int playerId) {
        super.setUp(numberOfPlayers, playerId);
        java.lang.Thread.currentThread().setContextClassLoader(
            java.lang.ClassLoader.getSystemClassLoader()
        );
    }

    public RiskAction computeNextAction(final Risk game, final long computationTime, final TimeUnit timeUnit) {

        RlEnv rlEnv = new RiskRlEnvWrapper(game);
        initialize(rlEnv.getObservation().getShapes(),rlEnv.getActionSpace().size(),true,Path.of(MODEL_PATH));
        final int nextMoveInt = agent.chooseAction(rlEnv, this.training).singletonOrThrow().getInt();
        RiskAction bestAction = (RiskAction) game.getPossibleActions().toArray()[nextMoveInt];

        assert game.isValidAction(bestAction);
        this.log.debugf("Found best move: %s", bestAction.toString());
        return bestAction;
    }

    public void tearDown() {
    }

    public void destroy() {
    }

    public static DefaultTrainingConfig setupTrainingConfig() {
        return new DefaultTrainingConfig(Loss.l2Loss())
            .optOptimizer(Adam.builder().optLearningRateTracker(Tracker.fixed(1e-6f)).build())
            .addEvaluator(new Accuracy())
            //.optInitializer(new NormalInitializer())
            .addTrainingListeners(TrainingListener.Defaults.basic());
    }
}
