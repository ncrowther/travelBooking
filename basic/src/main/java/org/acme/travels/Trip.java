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

import java.util.Date;

public class Trip {

    private String city;
    private String country;
    private Date begin;
    private Date end;
    private boolean visaRequired;
    private boolean banned;
    private String travelAdvisory;

    public Trip() {

    }

    public Trip(String city, String country, Date begin, Date end) {
        super();
        this.city = city;
        this.country = country;
        this.begin = begin;
        this.end = end;
        this.banned = false;
        this.travelAdvisory = "No advisory";
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public boolean isVisaRequired() {
        return visaRequired;
    }

    public void setVisaRequired(boolean visaRequired) {
        this.visaRequired = visaRequired;
    }

    public String getTravelAdvisory() {
        return travelAdvisory;
    }

    public void setTravelAdvisory(String travelAdvisory) {
        this.travelAdvisory = travelAdvisory;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    @Override
    public String toString() {
        return "Trip [begin=" + begin + ", Banned=" + banned + ", city=" + city + ", country=" + country
                + ", end=" + end + ", travelAdvisory=" + travelAdvisory + ", visaRequired=" + visaRequired + "]";
    }

}
