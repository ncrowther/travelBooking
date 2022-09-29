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
package org.acme.travels;

import org.acme.travels.rpa.api.PasswordEncryption;

public class RpaConfig {

    private String baseURL = "https://uk1api.wdgautomation.com";
    private String tenantId = "e780ec1f-e62f-4148-8335-2f3ac251373e";
    private String processName = "Traveladvisoryprocess";
    private String username = "";
    private String password = "";
    private int waitSeconds = 60;

    public RpaConfig(String baseURL,
            String tenantId,
            String username,
            String password,
            String processName,
            int waitSeconds) {
        this.baseURL = baseURL;
        this.tenantId = tenantId;
        this.username = username;
        this.password = password;
        this.processName = processName;
        this.waitSeconds = waitSeconds;
    }

    public RpaConfig() {
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String encryptedPassword) throws Exception {
        PasswordEncryption passwordEncryption = new PasswordEncryption();
        this.password = passwordEncryption.decrypt(encryptedPassword);
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public int getWaitSeconds() {
        return waitSeconds;
    }

    public void setWaitSeconds(int waitSeconds) {
        this.waitSeconds = waitSeconds;
    }

    @Override
    public String toString() {
        return "RpaConfig [baseURL=" + baseURL + ", password=" + password + ", processName=" + processName
                + ", tenantId=" + tenantId + ", username=" + username + ", waitSeconds=" + waitSeconds + "]";
    }

    public static void main(String args[]) {
        RpaConfig rpaConfig = new RpaConfig();

        rpaConfig.setBaseURL("https://uk1api.wdgautomation.com");
        rpaConfig.setTenantId("e780ec1f-e62f-4148-8335-2f3ac251373e");
        rpaConfig.setUsername("ncrowther@uk.ibm.com");
        try {
            rpaConfig.setPassword("/jNRZmrTtuoKqYqRIE8mtQ==");
        } catch (Exception e) {
            e.printStackTrace();
        }
        rpaConfig.setProcessName("Travelavisoryprocess");
        rpaConfig.setWaitSeconds(60);

        rpaConfig.toString();
    }

}
