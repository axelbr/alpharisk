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

package at.ac.tuwien.ifs.sge.agent.alpharisk.play;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import at.ac.tuwien.ifs.sge.agent.alpharisk.config.MuZeroConfig;

import at.ac.tuwien.ifs.sge.game.risk.board.RiskAction;
import org.jetbrains.annotations.NotNull;


public interface MuZeroAction {

    static NDArray encodeEmptyNDArray(@NotNull MuZeroConfig config, @NotNull NDManager nd) {
        return nd.zeros(new Shape(1, config.getBoardHeight(), config.getBoardWidth()));
    }

    int getIndex();


     NDArray encode(NDManager nd);

    void setIndex(int index);


}

