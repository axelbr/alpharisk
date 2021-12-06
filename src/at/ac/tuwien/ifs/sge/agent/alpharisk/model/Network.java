/*
 *  Copyright (c) 2021 enpasos GmbH
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package at.ac.tuwien.ifs.sge.agent.alpharisk.model;

import ai.djl.Device;
import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.*;
import ai.djl.training.Trainer;
import ai.djl.translate.TranslateException;
import at.ac.tuwien.ifs.sge.agent.alpharisk.config.MuZeroConfig;
import at.ac.tuwien.ifs.sge.agent.alpharisk.model.djl.SubModel;
import at.ac.tuwien.ifs.sge.agent.alpharisk.model.djl.blocks.atraining.MuZeroBlock;
import at.ac.tuwien.ifs.sge.agent.alpharisk.model.djl.blocks.binference.InitialInferenceBlock;
import at.ac.tuwien.ifs.sge.agent.alpharisk.model.djl.blocks.binference.InitialInferenceListTranslator;
import at.ac.tuwien.ifs.sge.agent.alpharisk.model.djl.blocks.binference.RecurrentInferenceBlock;
import at.ac.tuwien.ifs.sge.agent.alpharisk.model.djl.blocks.binference.RecurrentInferenceListTranslator;
import at.ac.tuwien.ifs.sge.agent.alpharisk.model.djl.blocks.cmainfunctions.DynamicsBlock;
import at.ac.tuwien.ifs.sge.agent.alpharisk.model.djl.blocks.cmainfunctions.PredictionBlock;
import at.ac.tuwien.ifs.sge.agent.alpharisk.model.djl.blocks.cmainfunctions.RepresentationBlock;
import at.ac.tuwien.ifs.sge.agent.alpharisk.gamebuffer.MuZeroGame;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static at.ac.tuwien.ifs.sge.agent.alpharisk.MuZero.getNetworksBasedir;
import static at.ac.tuwien.ifs.sge.agent.alpharisk.play.PlayManager.getAllActionsOnDevice;

@Data
public class Network {

    MuZeroConfig config;
    Model model;
    private SubModel representation;
    private SubModel prediction;
    private SubModel dynamics;

    private SubModel initialInference;
    private SubModel recurrentInference;

    private List<NDArray> actionSpaceOnDevice;

    public Network(@NotNull MuZeroConfig config, @NotNull Model model, Path modelPath) {
        this.model = model;
        this.config = config;

        if (model.getBlock() == null) {
            MuZeroBlock block = new MuZeroBlock(config);
            model.setBlock(block);
            try {
                model.load(modelPath);
            } catch (@NotNull IOException | MalformedModelException e) {
                e.printStackTrace();
            }
        }

      //  actionSpaceOnDevice = getAllActionsOnDevice(config, model.getNDManager());

        RepresentationBlock representationBlock = (RepresentationBlock) model.getBlock().getChildren().get("01Representation");
        PredictionBlock predictionBlock = (PredictionBlock) model.getBlock().getChildren().get("02Prediction");
        DynamicsBlock dynamicsBlock = (DynamicsBlock) model.getBlock().getChildren().get("03Dynamics");


        representation = new SubModel("representation", model, representationBlock);
        prediction = new SubModel("prediction", model, predictionBlock);
        dynamics = new SubModel("dynamics", model, dynamicsBlock);

        initialInference = new SubModel("initialInference", model, new InitialInferenceBlock(representationBlock, predictionBlock));
        recurrentInference = new SubModel("recurrentInference", model, new RecurrentInferenceBlock(dynamicsBlock, predictionBlock));
    }

    public void initActionSpaceOnDevice(NDManager ndManager) {
        actionSpaceOnDevice = getAllActionsOnDevice(config, ndManager);
    }

    public Network(@NotNull MuZeroConfig config, @NotNull Model model) {
        this(config, model, Paths.get(getNetworksBasedir(config)));
    }

    public static double getDoubleValue(@NotNull Model model, String name) {
        double epoch = 0;
        String prop = model.getProperty(name);
        if (prop != null) {
            epoch = Double.parseDouble(prop);
        }
        return epoch;
    }

    public static int getEpoch(@NotNull Model model) {
        int epoch = 0;
        String prop = model.getProperty("Epoch");
        if (prop != null) {
            epoch = Integer.parseInt(prop);
        }
        return epoch;
    }
    public void setHiddenStateNDManager(NDManager hiddenStateNDManager) {
        setHiddenStateNDManager(hiddenStateNDManager, true);
    }
    public void createAndSetHiddenStateNDManager(NDManager parentNDManager, boolean force) {
        if (force || initialInference.hiddenStateNDManager == null) {
            NDManager newHiddenStateNDManager = null;
            if (!config.hiddenStateRemainOnGPU) {
                newHiddenStateNDManager = parentNDManager.newSubManager(Device.cpu());
            } else {
                newHiddenStateNDManager = parentNDManager.newSubManager(Device.gpu());
            }
            initialInference.hiddenStateNDManager = newHiddenStateNDManager;
            recurrentInference.hiddenStateNDManager = newHiddenStateNDManager;
        }
    }
    public void setHiddenStateNDManager(NDManager hiddenStateNDManager, boolean force) {
        if (force || initialInference.hiddenStateNDManager == null) {
            initialInference.hiddenStateNDManager = hiddenStateNDManager;
            recurrentInference.hiddenStateNDManager = hiddenStateNDManager;
        }
    }

    public NDManager getNDManager() {
        return model.getNDManager();
    }


    public NetworkIO initialInferenceDirect(@NotNull MuZeroGame game) {
        return Objects.requireNonNull(initialInferenceListDirect(List.of(game))).get(0);
    }


    public @Nullable List<NetworkIO> initialInferenceListDirect(List<MuZeroGame> gameList) {

        List<NetworkIO> networkOutputFromInitialInference = null;

        InitialInferenceListTranslator translator = new InitialInferenceListTranslator();
        try (Predictor<List<MuZeroGame>, List<NetworkIO>> predictor = initialInference.newPredictor(translator)) {
            networkOutputFromInitialInference = predictor.predict(gameList);

        } catch (TranslateException e) {
            e.printStackTrace();
        }
        return networkOutputFromInitialInference;


    }


    public @Nullable List<NetworkIO> recurrentInferenceListDirect(@NotNull List<NDArray> hiddenStateList, List<NDArray> actionList) {
        NetworkIO networkIO = new NetworkIO();
        NDArray hiddenState = NDArrays.stack(new NDList(hiddenStateList));
        networkIO.setHiddenState(hiddenState);
        networkIO.setActionList(actionList);

        networkIO.setConfig(config);


        List<NetworkIO> networkOutput = null;

        RecurrentInferenceListTranslator translator = new RecurrentInferenceListTranslator();
        try (Predictor<NetworkIO, List<NetworkIO>> predictorRepresentation = recurrentInference.newPredictor(translator)) {
            networkOutput = predictorRepresentation.predict(networkIO);
        } catch (TranslateException e) {
            e.printStackTrace();
        }

        hiddenState.close();
        return networkOutput;
    }

    public int trainingSteps() {
        return getEpoch(model) * config.getNumberOfTrainingStepsPerEpoch();
    }

    public void debugDump() {
     //   ((BaseNDManager) this.getModel().getNDManager()).debugDump(0);
    }

    public static void debugDumpFromTrainer(Trainer trainer) {
      //  ((BaseNDManager) trainer.getModel().getNDManager()).debugDump(0);
    }

}
