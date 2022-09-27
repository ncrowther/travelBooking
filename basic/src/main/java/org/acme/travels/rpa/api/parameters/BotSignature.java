/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.acme.travels.rpa.api.parameters;

import java.util.ArrayList;
import java.util.List;

public class BotSignature {

    String botScriptName = "Undefined";
    List<RpaParameter> parameters = new ArrayList<RpaParameter>();

    public BotSignature(String botName) {
        this.botScriptName = botName;
    }

    public String getBotScriptName() {
        return botScriptName;
    }

    public void setBotScriptName(String botScriptName) {
        this.botScriptName = botScriptName;
    }

    public List<RpaParameter> getParameters() {
        return parameters;
    }

    public void addParameter(RpaParameter parameter) {
        parameters.add(parameter);
    }

}
