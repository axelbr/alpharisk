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


import ai.djl.ndarray.NDArray;
import at.ac.tuwien.ifs.sge.agent.alpharisk.config.MuZeroConfig;
import at.ac.tuwien.ifs.sge.agent.alpharisk.play.MuZeroAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NetworkIO {

    private float[] policyValues;
    private double value;

    private double reward;

    private NDArray hiddenState;
    private MuZeroAction action;
    private MuZeroConfig config;


    private List<NDArray> actionList;

}
