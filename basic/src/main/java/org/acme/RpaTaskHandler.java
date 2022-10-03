/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.acme;

import java.util.HashMap;
import java.util.Map;

import org.acme.travels.rpa.api.PasswordEncryption;
import org.acme.travels.rpa.api.RpaApi;
import org.acme.travels.rpa.api.RpaResponse;
import org.kie.api.runtime.process.ProcessWorkItemHandlerException;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;

public class RpaTaskHandler implements KogitoWorkItemHandler {

    static final String COMPLETED_STATUS = "done";

    private String baseUrl;
    private String tenantId;
    private String username;
    private String password;
    private String processName;
    private Integer waitSeconds;
    private String payload;

    @Override
    public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {

        validateParameters(workItem);

        RpaResponse result = callRpa(baseUrl, tenantId, username, password, processName, waitSeconds, payload);

        Map<String, Object> results = new HashMap<String, Object>();

        boolean botFinished = result.getResponseStatus().equals(COMPLETED_STATUS);
        results.put("Status", botFinished);
        results.put("ResponsePayload", result.getResponsePayload());

        // Don’t forget to finish the work item otherwise the process
        // will be active infinitely and never will pass the flow
        // to the next node.
        manager.completeWorkItem(workItem.getStringId(), results);
    }

    @Override
    public void abortWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
        manager.abortWorkItem(workItem.getStringId());
    }

    private RpaResponse callRpa(String baseUrl,
            String tenantId,
            String username,
            String password,
            String processName,
            int waitSeconds,
            String payload) {

        RpaResponse result = null;

        try {
            result = RpaApi.startProcessAndWait(baseUrl,
                    tenantId,
                    username,
                    password,
                    processName,
                    payload,
                    waitSeconds);

            String status = result.getResponseStatus();
            System.out.println("Expected: " + COMPLETED_STATUS + ", Actual: " + status);

        } catch (Exception e) {
            handleError(e.toString());
        }

        return result;
    }

    private void validateParameters(KogitoWorkItem workItem) {

        System.out.println("RpaTaskHandler Passed parameters:");

        // Printing task’s parameters, it will also print
        // our value we pass to the task from the process
        for (String parameter : workItem.getParameters().keySet()) {
            Object parameterValue = workItem.getParameters().get(parameter);
            System.out.println(parameter + " = " + parameterValue + ": " + parameterValue.getClass());
        }

        this.baseUrl = (String) workItem.getParameter("BaseURL");
        if (this.baseUrl == null || this.baseUrl.equals("")) {
            System.out.println("ERROR: BaseUrl is null or empty");
            handleError("baseUrl empty");
        }

        this.tenantId = (String) workItem.getParameter("TenantId");
        if (this.tenantId == null || this.tenantId.equals("")) {
            System.out.println("ERROR: TenantId is null or empty");
            handleError("TenantId empty");
        }

        this.username = (String) workItem.getParameter("Username");
        if (this.username == null || this.username.equals("")) {
            System.out.println("ERROR: Username is null or empty");
            handleError("Username empty");
        }

        this.password = (String) workItem.getParameter("Password");
        if (this.password == null || this.password.equals("")) {
            System.out.println("ERROR: Password is null or empty");
            handleError("Password empty");
        }

        // Decrypt password from Base64
        PasswordEncryption passwordEncryption;
        try {
            passwordEncryption = new PasswordEncryption();
            this.password = passwordEncryption.decrypt(this.password);
        } catch (Exception e) {
            System.out.println("ERROR: Failed to decrypt Password from Base64");
            handleError("Failed to decrypt Password from Base64");
        }

        this.processName = (String) workItem.getParameter("ProcessName");
        if (this.processName == null || this.processName.equals("")) {
            System.out.println("ERROR: ProcessName is null or empty");
            handleError("ProcessName empty");
        }

        this.waitSeconds = (Integer) workItem.getParameter("WaitSeconds");
        if (this.waitSeconds == null) {
            System.out.println("ERROR: WaitSeconds is null");
            handleError("WaitSeconds null");
        }

        this.payload = (String) workItem.getParameter("Payload");
        if (this.payload == null || this.processName.equals("")) {
            System.out.println("ERROR: payload is null or empty");
            handleError("Payload empty");
        }
    }

    private void handleError(String strategy) {
        throw new ProcessWorkItemHandlerException("error_handling",
                ProcessWorkItemHandlerException.HandlingStrategy.valueOf(strategy),
                new IllegalStateException(strategy + " strategy test"));
    }
}
