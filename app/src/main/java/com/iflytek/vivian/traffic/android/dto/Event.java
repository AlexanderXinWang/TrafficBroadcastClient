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

import java.util.Date;

public class Event {
    private String id;
    private String policemanId;
    private String policemanName;
    private String location;
    private String vehicle;
    private String event;
    private String eventResult;
    private String iatResult;
    private Date startTime;
    private byte[] pcm;

    public Event(String policemanName, String location, String event, Date startTime) {
        this.policemanName = policemanName;
        this.location = location;
        this.event = event;
        this.startTime = startTime;
    }

    public Event(String id, String policemanId, String policemanName, String location, String vehicle, String event, String eventResult, String iatResult, Date startTime, byte[] mp3) {
        this.id = id;
        this.policemanId = policemanId;
        this.policemanName = policemanName;
        this.location = location;
        this.vehicle = vehicle;
        this.event = event;
        this.eventResult = eventResult;
        this.iatResult = iatResult;
        this.startTime = startTime;
        this.pcm = pcm;
    }

    public Event() {

    }

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

    public String getIatResult() {
        return iatResult;
    }

    public void setIatResult(String iatResult) {
        this.iatResult = iatResult;
    }

    public byte[] getMp3() {
        return pcm;
    }

    public void setMp3(byte[] mp3) {
        this.pcm = pcm;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getPolicemanName() {
        return policemanName;
    }

    public void setPolicemanName(String policemanName) {
        this.policemanName = policemanName;
    }

    @Override
    public String toString() {
        return String.format("地点：[%s]，事件：[%s]", location, event);
    }

}
