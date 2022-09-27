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

public class RpaParameter {

    private ParameterDirection direction;
    private String objectType;
    private String name;
    private String examplePayload = "String";

    public RpaParameter(ParameterDirection direction, String name, String objectType) {
        this.direction = direction;
        this.name = name;
        this.objectType = objectType;
    }

    public RpaParameter(ParameterDirection direction, String name, String objectType, String examplePayload) {
        super();
        this.direction = direction;
        this.objectType = objectType;
        this.name = name;
        this.examplePayload = examplePayload;
    }

    @Override
    public String toString() {
        return "RpaParameter [direction=" + direction + ", objectType=" + objectType + ", name=" + name
                + ", examplePayload=" + examplePayload + "]";
    }

    public ParameterDirection getDirection() {
        return direction;
    }

    public void setDirection(ParameterDirection direction) {
        this.direction = direction;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExamplePayload() {
        return examplePayload;
    }

    public void setExamplePayload(String examplePayload) {
        this.examplePayload = examplePayload;
    }

}
