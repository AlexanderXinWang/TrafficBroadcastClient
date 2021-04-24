package com.iflytek.vivian.traffic.android.event.event;

import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

import java.util.List;

public class EventListEvent extends BaseNetworkEvent<List<Event>> {
    public EventListEvent(boolean success, String errorMessage, Exception e, List<Event> data) {
        super(success, errorMessage, e, data);
    }

    public static EventListEvent success(List<Event> eventList) {
        return new EventListEvent(true, null, null, eventList);
    }

    public static EventListEvent fail(Exception e, String errorMessage) {
        return new EventListEvent(false, errorMessage, e, null);
    }
}
