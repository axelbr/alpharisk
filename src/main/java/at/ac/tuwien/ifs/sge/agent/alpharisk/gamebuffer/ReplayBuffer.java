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

package at.ac.tuwien.ifs.sge.agent.alpharisk.gamebuffer;


import ai.djl.ndarray.NDManager;
import at.ac.tuwien.ifs.sge.agent.alpharisk.MuZero;
import at.ac.tuwien.ifs.sge.agent.alpharisk.config.MuZeroConfig;
import at.ac.tuwien.ifs.sge.agent.alpharisk.model.Sample;
import at.ac.tuwien.ifs.sge.agent.alpharisk.environment.OneOfTwoPlayer;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static at.ac.tuwien.ifs.sge.agent.alpharisk.gamebuffer.GameIO.getLatestBufferNo;


@Data
public class ReplayBuffer {
    private int batchSize;
    private ReplayBufferDTO buffer;

    private MuZeroConfig config;

    public ReplayBuffer(@NotNull MuZeroConfig config) {
        this.config = config;
        this.batchSize = config.getBatchSize();
        this.buffer = new ReplayBufferDTO(config.getWindowSize());
    }

    public static @NotNull Sample sampleFromGame(int numUnrollSteps, int tdSteps, @NotNull MuZeroGame game, NDManager ndManager, ReplayBuffer replayBuffer, MuZeroConfig config) {
        int gamePos = samplePosition(game);
        if (gamePos == 0) {
            int i = 42;
        }
        return sampleFromGame(numUnrollSteps, tdSteps, game, gamePos, ndManager, replayBuffer, config);
    }


    public static @NotNull Sample sampleFromGame(int numUnrollSteps, int tdSteps, @NotNull MuZeroGame game, int gamePos, NDManager ndManager, ReplayBuffer replayBuffer, MuZeroConfig config) {
        Sample sample = new Sample();
        game.replayToPosition(gamePos);

        sample.setObservation(game.getObservation(ndManager));

        List<Integer> actions = new ArrayList<>(game.getGameDTO().getActionHistory());
        if (actions.size() < gamePos + numUnrollSteps) {
            actions.addAll(game.getRandomActionsIndices(gamePos + numUnrollSteps - actions.size()));

        }


        sample.setActionsList(actions.subList(gamePos, gamePos + numUnrollSteps));


        sample.setTargetList(game.makeTarget(gamePos, numUnrollSteps, tdSteps, game.toPlay(), sample, config));

        return sample;
    }

//    private boolean existsGameStateWithPositiveResult(Game game, int pos, OneOfTwoPlayer player) {
//
//
//        StateNode base = this.buffer.gameTree.findNode(game.getGameDTO().getActionHistory(), pos);
//        return base.hasOrIsLeafNodeWithPositivResult(player);
//    }


    public static int samplePosition(@NotNull MuZeroGame game) {
        int numActions = game.getGameDTO().getActionHistory().size();
        return ThreadLocalRandom.current().nextInt(0, numActions + 1);  // one more positions than actions
    }

    public static @NotNull ReplayBufferDTO decodeDTO(byte @NotNull [] bytes) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);


        try (ObjectInputStream objectInputStream = new ObjectInputStream(new GZIPInputStream(byteArrayInputStream))) {
            return (ReplayBufferDTO) objectInputStream.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public static byte @NotNull [] encodeDTO(ReplayBufferDTO dto) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new GZIPOutputStream(byteArrayOutputStream))) {
            objectOutputStream.writeObject(dto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return byteArrayOutputStream.toByteArray();
    }


    public void saveGame(@NotNull MuZeroGame game) {


        buffer.saveGame(game, config);

    }

    /**
     * @param numUnrollSteps number of actions taken after the chosen position (if there are any)
     */
    public List<Sample> sampleBatch(int numUnrollSteps, int tdSteps, NDManager ndManager) {

        return sampleGames().stream()
                .map(game -> sampleFromGame(numUnrollSteps, tdSteps, game, ndManager, this, config))
                .collect(Collectors.toList());

    }


    // for "fair" training do train the strengths of PlayerA and PlayerB equally
    public List<MuZeroGame> sampleGames() {

//        long start = System.currentTimeMillis();

        List<MuZeroGame> games = new ArrayList<>(this.buffer.getGames());
        Collections.shuffle(games);


        List<MuZeroGame> gamesToTrain = new ArrayList<>();
        gamesToTrain.addAll(games.stream()
                .filter(g -> {
                    if (g instanceof ZeroSumGame) {
                        Optional<OneOfTwoPlayer> winner = ((ZeroSumGame)g).whoWonTheGame();
                        return winner.isEmpty() || winner.get() == OneOfTwoPlayer.PlayerA;
                    } else {
                        return true;
                    }
                })
                .limit(this.batchSize / 2)

                .collect(Collectors.toList()));
        int numberOfTrainingGamesForA = gamesToTrain.size();
        //  log.debug("number of training games for A: " + numberOfTrainingGamesForA);
        games.removeAll(gamesToTrain);  // otherwise draw games could be selected again
        gamesToTrain.addAll(games.stream()
                .filter(g -> {
                    if (g instanceof ZeroSumGame) {
                        Optional<OneOfTwoPlayer> winner = ((ZeroSumGame)g).whoWonTheGame();
                        return winner.isEmpty() || winner.get() == OneOfTwoPlayer.PlayerB;
                    } else {
                        return true;
                    }
                })
                .limit(this.batchSize / 2)
                .collect(Collectors.toList()));


        int numberOfTrainingGamesForB = gamesToTrain.size() - numberOfTrainingGamesForA;
        // log.debug("number of training games for B: " + numberOfTrainingGamesForB);
        return gamesToTrain;
    }

    public void saveState() {
        String pathname = MuZero.getGamesBasedir(config) + "/buffer" + buffer.getCounter();
        System.out.println("saving ... " + pathname);


        try {
            FileUtils.writeByteArrayToFile(new File(pathname), encodeDTO(this.buffer));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadLatestState() {
        int c = getLatestBufferNo(config);
        loadState(c);
    }

    public void loadState(int c) {
        String pathname = MuZero.getGamesBasedir(config) + "/buffer" + c;
        System.out.println("loading ... " + pathname);
        try {
            byte[] raw = FileUtils.readFileToByteArray(new File(pathname));
            this.buffer = decodeDTO(raw);
            rebuildGames();
            this.buffer.setWindowSize(config.getWindowSize());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void rebuildGames() {
        buffer.games = new ArrayList<>();
        for (GameDTO gameDTO : buffer.getData()) {
            MuZeroGame game = this.config.newGame();
            game.setGameDTO(gameDTO);
            if (!game.terminal()) {
                game.replayToPosition(game.actionHistory().getActionIndexList().size());
            }
            buffer.games.add(game);
        }
    }


}
