package com.iflytek.vivian.traffic.android.event.event;

import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

import java.util.List;

public class EventListByEventDescEvent extends BaseNetworkEvent<List<Event>> {
    public EventListByEventDescEvent(boolean success, String errorMessage, Exception e, List<Event> data) {
        super(success, errorMessage, e, data);
    }

    public static EventListByEventDescEvent success(List<Event> eventList) {
        return new EventListByEventDescEvent(true, null, null, eventList);
    }

    public static EventListByEventDescEvent fail(Exception e, String errorMessage) {
        return new EventListByEventDescEvent(false, errorMessage, e, null);
    }
}
