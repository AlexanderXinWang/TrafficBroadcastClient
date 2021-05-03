package com.iflytek.vivian.traffic.android.event.event;

import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

import java.util.List;

public class EventListByLocationAscEvent extends BaseNetworkEvent<List<Event>> {
    public EventListByLocationAscEvent(boolean success, String errorMessage, Exception e, List<Event> data) {
        super(success, errorMessage, e, data);
    }

    public static EventListByLocationAscEvent success(List<Event> eventList) {
        return new EventListByLocationAscEvent(true, null, null, eventList);
    }

    public static EventListByLocationAscEvent fail(Exception e, String errorMessage) {
        return new EventListByLocationAscEvent(false, errorMessage, e, null);
    }
}
