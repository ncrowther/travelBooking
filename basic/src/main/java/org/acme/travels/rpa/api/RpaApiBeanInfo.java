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

import java.beans.MethodDescriptor;
import java.beans.ParameterDescriptor;
import java.beans.SimpleBeanInfo;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class RpaApiBeanInfo extends SimpleBeanInfo {
    private Class beanClass = RpaApi.class;

    public MethodDescriptor[] getMethodDescriptors() {
        try {
            MethodDescriptor startProcessAndWaitDescriptor = startProcessAndWaitMethod();

            return new MethodDescriptor[] { startProcessAndWaitDescriptor };
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.getMethodDescriptors();
    }

    // public static String startProcessAndWait(String baseUrl, String tenantId, String username, String password, String processName, String payload, Integer waitSeconds) throws RpaApiException, InterruptedException {
    private MethodDescriptor startProcessAndWaitMethod() throws NoSuchMethodException {
        Method method = this.beanClass.getMethod("startProcessAndWait",
                new Class[] { String.class, String.class, String.class, String.class, String.class, String.class, Integer.class });
        if (method == null) {
            System.out.println("Unable to find startProcessAndWait method.");
            return null;
        }

        ArrayList al = new ArrayList();

        ParameterDescriptor param = new ParameterDescriptor();
        param.setShortDescription("Base Url");
        param.setDisplayName("baseUrl");
        al.add(param);

        param = new ParameterDescriptor();
        param.setShortDescription("Tenant Id");
        param.setDisplayName("tenantId");
        al.add(param);

        param = new ParameterDescriptor();
        param.setShortDescription("Username");
        param.setDisplayName("username");
        al.add(param);

        param = new ParameterDescriptor();
        param.setShortDescription("Password");
        param.setDisplayName("password");
        al.add(param);

        param = new ParameterDescriptor();
        param.setShortDescription("Process Name");
        param.setDisplayName("processName");
        al.add(param);

        param = new ParameterDescriptor();
        param.setShortDescription("payload");
        param.setDisplayName("payload");
        al.add(param);

        param = new ParameterDescriptor();
        param.setShortDescription("Wait period in Seconds");
        param.setDisplayName("waitSeconds");
        al.add(param);

        MethodDescriptor methodDescriptor = new MethodDescriptor(method,
                (ParameterDescriptor[]) al.toArray(new ParameterDescriptor[0]));
        return methodDescriptor;
    }

    public static void main(String[] args) {

        // Run  to test bean introspection

        RpaApiBeanInfo test = new RpaApiBeanInfo();

        System.out.println(test.getMethodDescriptors()[0]);

    }
}