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

import org.acme.travels.rpa.json.JsonUtils;

public class RpaResponse {
    private String responseStatus;
    private String responsePayload;

    public RpaResponse(String response) {
        this.responseStatus = JsonUtils.getStatus(response);
        this.responsePayload = JsonUtils.getResultPayload(response);
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public String getResponsePayload() {
        return responsePayload;
    }

    @Override
    public String toString() {
        return "RpaResponse [responsePayload=" + responsePayload + ", responseStatus=" + responseStatus + "]";
    }

    public static void main(String args[]) {
        String rpaResponseStr =
                "{\"status\":\"done\",\"variables\":[{\"value\":\"Russian Federation\",\"name\":\"in_destination\"}],\"outputs\":{\"out_travelAdvice\":\"Russia - Level 4: Do Not Travel\",\"out_doNotTravel\":true}}";

        org.acme.travels.rpa.api.RpaResponse responseParser = new org.acme.travels.rpa.api.RpaResponse(rpaResponseStr);
        String status = responseParser.getResponseStatus();
        String responsePayload = responseParser.getResponsePayload();

        System.out.println("Status: " + status);
        System.out.println("responsePayload: " + responsePayload);

        String advice = org.acme.travels.rpa.json.JsonUtils.getString("out_travelAdvice", responsePayload);
        Boolean travel = org.acme.travels.rpa.json.JsonUtils.getBoolean("out_doNotTravel", responsePayload);

        System.out.println("Travel advice: " + advice);
        System.out.println("Don't travel: " + travel);
    }

}
