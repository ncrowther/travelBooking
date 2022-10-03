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
package org.acme.travels.rpa.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class RpaRequest {
    Map<String, Object> parameters = new HashMap<String, Object>();

    public String getParameters() {

        StringBuilder payload = new StringBuilder("{ \"payload\":{");

        Iterator<Entry<String, Object>> it = parameters.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            String key = entry.getKey();
            Object value = entry.getValue();

            payload.append("\"" + key + "\":\"" + value + "\"");

            if (it.hasNext()) {
                payload.append(", ");
            }
        }

        payload.append("}}");
        return payload.toString();
    }

    public void setParameter(String name, Object value) {
        this.parameters.put(name, value);
    }

    public static void main(String args[]) {

        RpaRequest request = new RpaRequest();
        request.setParameter("in_destination", "Russian Federation");

        System.out.println("Request: " + request.getParameters());
    }
}
