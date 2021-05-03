package com.iflytek.vivian.traffic.android.event.event;

import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

import java.util.List;

public class EventListByLocationDescEvent extends BaseNetworkEvent<List<Event>> {
    public EventListByLocationDescEvent(boolean success, String errorMessage, Exception e, List<Event> data) {
        super(success, errorMessage, e, data);
    }

    public static EventListByLocationDescEvent success(List<Event> eventList) {
        return new EventListByLocationDescEvent(true, null, null, eventList);
    }

    public static EventListByLocationDescEvent fail(Exception e, String errorMessage) {
        return new EventListByLocationDescEvent(false, errorMessage, e, null);
    }
}
