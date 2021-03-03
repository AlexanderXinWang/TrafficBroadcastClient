/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
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
 *
 */

package com.iflytek.vivian.traffic.android.dto;

public class Event {
    private String id;
    private String policemanId;
    private String location;
    private String vehicle;
    private String event;
    private String eventResult;
    private String astResult;
    private byte[] mp3;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPolicemanId() {
        return policemanId;
    }

    public void setPolicemanId(String policemanId) {
        this.policemanId = policemanId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEventResult() {
        return eventResult;
    }

    public void setEventResult(String eventResult) {
        this.eventResult = eventResult;
    }

    public String getAstResult() {
        return astResult;
    }

    public void setAstResult(String astResult) {
        this.astResult = astResult;
    }

    public byte[] getMp3() {
        return mp3;
    }

    public void setMp3(byte[] mp3) {
        this.mp3 = mp3;
    }

    @Override
    public String toString() {
        return String.format("地点：[%s]，事件：[%s]", location, event);
    }

}
