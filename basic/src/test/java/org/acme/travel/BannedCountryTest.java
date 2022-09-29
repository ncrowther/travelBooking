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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.acme.travels.Trip;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class BannedCountryTest {

    @Inject
    @Named("bannedCountryCheck")
    Process<? extends Model> bannedCountryProcess;

    @Test
    public void testBookingFlight() {

        assertNotNull(bannedCountryProcess);

        Model m = bannedCountryProcess.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("trip", new Trip("Moscow", "Russian Federation", new Date(), new Date()));

        m.fromMap(parameters);

        ProcessInstance<?> processInstance = bannedCountryProcess.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, processInstance.status());

        Model result = (Model) processInstance.variables();
        assertEquals(2, result.toMap().size());
        Trip trip = (Trip) result.toMap().get("trip");
        assertNotNull(trip);
        assertEquals(true, trip.isBanned());
        assertEquals("Russia - Level 4: Do Not Travel", trip.getTravelAdvisory());
    }
}
