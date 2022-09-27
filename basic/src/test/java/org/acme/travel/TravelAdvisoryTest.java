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
package org.acme.travel;

import javax.inject.Inject;

import org.acme.travels.rpa.api.RpaApi;
import org.acme.travels.rpa.json.JsonUtils;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieRuntimeBuilder;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class TravelAdvisoryTest {

    @Inject
    KieRuntimeBuilder ruleRuntime;

    static final String baseURL = "https://uk1api.wdgautomation.com";
    static final String tenantId = "e780ec1f-e62f-4148-8335-2f3ac251373e";
    static final String username = "ncrowther@uk.ibm.com";
    static final String password = "Porker01!";
    static final String processName = "Traveladvisoryprocess";
    static final String payload = "{ \"payload\": { \"in_destination\": \"RussianFederation\", }}";
    static final String COMPLETED_STATUS = "done";
    static final int waitSeconds = 30;

    @Test
    public void testVisaNotRequiredRule() {

        try {
            String result = RpaApi.startProcessAndWait(baseURL, tenantId, username, password, processName, payload, waitSeconds);

            assertNotNull(result, "Result not null");

            String status = JsonUtils.getStatus(result);

            assertEquals(COMPLETED_STATUS, status);

            Object outputVar = null;
            outputVar = JsonUtils.getResultVar(result, "out_doNotTravel");
            System.out.println(outputVar);
            assertTrue((boolean) outputVar);

            outputVar = JsonUtils.getResultVar(result, "out_travelAdvice");
            System.out.println(outputVar);
            assertEquals("Russia - Level 4: Do Not Travel", (String) outputVar);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}