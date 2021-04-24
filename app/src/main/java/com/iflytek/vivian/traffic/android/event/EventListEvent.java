package com.iflytek.vivian.traffic.android.event;

import com.iflytek.vivian.traffic.android.dto.Event;

import java.util.List;

public class EventListEvent extends BaseNetworkEvent {
    public EventListEvent(boolean success, String errorMessage, Exception e, Object data) {
        super(success, errorMessage, e, data);
    }

    public static EventListEvent success(List<Event> eventList) {
        return new EventListEvent(true, null, null, eventList);
    }

    public static EventListEvent fail(Exception e, String errorMessage) {
        return new EventListEvent(false, errorMessage, e, null);
    }
}
