package com.iflytek.vivian.traffic.android.event.event;

import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

import java.util.List;

public class EventListByEventAscEvent extends BaseNetworkEvent<List<Event>> {
    public EventListByEventAscEvent(boolean success, String errorMessage, Exception e, List<Event> data) {
        super(success, errorMessage, e, data);
    }

    public static EventListByEventAscEvent success(List<Event> eventList) {
        return new EventListByEventAscEvent(true, null, null, eventList);
    }

    public static EventListByEventAscEvent fail(Exception e, String errorMessage) {
        return new EventListByEventAscEvent(false, errorMessage, e, null);
    }
}
