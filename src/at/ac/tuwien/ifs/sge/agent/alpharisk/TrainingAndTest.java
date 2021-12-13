package at.ac.tuwien.ifs.sge.agent.alpharisk;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ai.djl.Model;
import ai.djl.modality.rl.agent.EpsilonGreedy;
import ai.djl.modality.rl.agent.RlAgent;
import ai.djl.modality.rl.env.RlEnv;
import ai.djl.ndarray.types.Shape;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.Trainer;
import ai.djl.training.tracker.LinearTracker;
import ai.djl.training.tracker.Tracker;
import at.ac.tuwien.ifs.sge.engine.Logger;
import at.ac.tuwien.ifs.sge.game.risk.board.Risk;

import static at.ac.tuwien.ifs.sge.agent.alpharisk.AlphaRisk.BATCH_SIZE;
import static at.ac.tuwien.ifs.sge.agent.alpharisk.AlphaRisk.EXPLORE;
import static at.ac.tuwien.ifs.sge.agent.alpharisk.AlphaRisk.FINAL_EPSILON;
import static at.ac.tuwien.ifs.sge.agent.alpharisk.AlphaRisk.INITIAL_EPSILON;
import static at.ac.tuwien.ifs.sge.agent.alpharisk.AlphaRisk.MODEL_PATH;
import static at.ac.tuwien.ifs.sge.agent.alpharisk.AlphaRisk.OBSERVE;
import static at.ac.tuwien.ifs.sge.agent.alpharisk.AlphaRisk.SAVE_EVERY_STEPS;
import static at.ac.tuwien.ifs.sge.agent.alpharisk.AlphaRisk.setupTrainingConfig;


public class TrainingAndTest {

    static RlEnv.Step[] batchSteps;

    public static void main(String[] args) {
        train();
    }

    public static void train() {

        RiskRlEnvWrapper game = new RiskRlEnvWrapper();
        AlphaRisk alphaRisk = new AlphaRisk(true);

        //DefaultTrainingConfig config = setupTrainingConfig();
        try (Trainer trainer = alphaRisk.trainer){
            trainer.initialize(new Shape(BATCH_SIZE, 4, 80, 80));
            trainer.notifyListeners(listener -> listener.onTrainingBegin(trainer));

            //RlAgent agent = new QAgent(trainer, REWARD_DISCOUNT);
            Tracker exploreRate =
                LinearTracker.builder()
                    .setBaseValue(INITIAL_EPSILON)
                    .optSlope(-(INITIAL_EPSILON - FINAL_EPSILON) / EXPLORE)
                    .optMinValue(FINAL_EPSILON)
                    .build();
            RlAgent agent = new EpsilonGreedy(alphaRisk.agent, exploreRate);

            int numOfThreads = 2;
            List<Callable<Object>> callables = new ArrayList<>(numOfThreads);
            callables.add(new GeneratorCallable(game, agent, alphaRisk.training));
            if(alphaRisk.training) {
                callables.add(new TrainerCallable(alphaRisk.model, agent));
            }
            ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);
            try {
                try {
                    List<Future<Object>> futures = new ArrayList<>();
                    for (Callable<Object> callable : callables) {
                        futures.add(executorService.submit(callable));
                    }
                    for (Future<Object> future : futures) {
                        future.get();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    alphaRisk.getLogger().printStackTrace(e);
                }
            } finally {
                executorService.shutdown();
            }
        }
    }

    private static class TrainerCallable implements Callable<Object> {
        private final RlAgent agent;
        private final Model model;

        public TrainerCallable(Model model, RlAgent agent) {
            this.model = model;
            this.agent = agent;
        }

        @Override
        public Object call() throws Exception {
            while (RiskRlEnvWrapper.trainStep < EXPLORE) {
                Thread.sleep(0);
                if (RiskRlEnvWrapper.gameStep > OBSERVE) {
                    this.agent.trainBatch(batchSteps);
                    RiskRlEnvWrapper.trainStep++;
                    if (RiskRlEnvWrapper.trainStep > 0 && RiskRlEnvWrapper.trainStep % SAVE_EVERY_STEPS == 0) {
                        model.save(Paths.get(MODEL_PATH), "dqn-" + RiskRlEnvWrapper.trainStep);
                    }
                }
            }
            return null;
        }
    }

    private static class GeneratorCallable implements Callable<Object> {
        private final RiskRlEnvWrapper game;
        private final RlAgent agent;
        private final boolean training;

        public GeneratorCallable(RiskRlEnvWrapper game, RlAgent agent, boolean training) {
            this.game = game;
            this.agent = agent;
            this.training = training;
        }

        @Override
        public Object call() {
            while (RiskRlEnvWrapper.trainStep < EXPLORE) {
                batchSteps = game.getBatch();//runEnvironment(agent, training);
            }
            return null;
        }
    }

}
