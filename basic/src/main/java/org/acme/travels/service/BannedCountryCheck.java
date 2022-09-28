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
package org.acme.travels.service;

import javax.enterprise.context.ApplicationScoped;

import org.acme.travels.Trip;
import org.acme.travels.rpa.api.RpaApi;
import org.acme.travels.rpa.json.JsonUtils;

@ApplicationScoped
public class BannedCountryCheck {

    static final String baseURL = "https://uk1api.wdgautomation.com";
    static final String tenantId = "e780ec1f-e62f-4148-8335-2f3ac251373e";
    static final String username = "ncrowther@uk.ibm.com";
    static final String password = "Porker01!";
    static final String processName = "Traveladvisoryprocess";
    static final String COMPLETED_STATUS = "done";
    static final int waitSeconds = 60;
    boolean doNotTravel = false;
    String travelAdvisory = "No Advisory";

    public Trip bannedCountryCheck(Trip trip) {
        try {
            String destination = trip.getCountry();

            final String payload = "{ \"payload\": { \"in_destination\": \"" + destination + "\", }}";

            String result = RpaApi.startProcessAndWait(baseURL, tenantId, username, password, processName, payload, waitSeconds);

            String status = JsonUtils.getStatus(result);

            System.out.println("Expected: " + COMPLETED_STATUS + ", Actual: " + status);

            Object doNotTravelObj = JsonUtils.getResultVar(result, "out_doNotTravel");
            doNotTravel = (boolean) doNotTravelObj;

            Object travelAdviceObj = JsonUtils.getResultVar(result, "out_travelAdvice");
            travelAdvisory = (String) travelAdviceObj;

            System.out.println("Travel Advice: " + travelAdvisory);

        } catch (Exception e) {
            e.printStackTrace();
        }

        trip.setBanned(doNotTravel);
        trip.setTravelAdvisory(travelAdvisory);
        return trip;
    }
}
