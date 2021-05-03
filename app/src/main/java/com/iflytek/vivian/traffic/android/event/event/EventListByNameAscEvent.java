package com.iflytek.vivian.traffic.android.event.event;

import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

import java.util.List;

public class EventListByNameAscEvent extends BaseNetworkEvent<List<Event>> {
    public EventListByNameAscEvent(boolean success, String errorMessage, Exception e, List<Event> data) {
        super(success, errorMessage, e, data);
    }

    public static EventListByNameAscEvent success(List<Event> eventList) {
        return new EventListByNameAscEvent(true, null, null, eventList);
    }

    public static EventListByNameAscEvent fail(Exception e, String errorMessage) {
        return new EventListByNameAscEvent(false, errorMessage, e, null);
    }
}
