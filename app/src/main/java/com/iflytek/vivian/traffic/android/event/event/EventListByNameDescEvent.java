package com.iflytek.vivian.traffic.android.event.event;

import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

import java.util.List;

public class EventListByNameDescEvent extends BaseNetworkEvent<List<Event>> {
    public EventListByNameDescEvent(boolean success, String errorMessage, Exception e, List<Event> data) {
        super(success, errorMessage, e, data);
    }

    public static EventListByNameDescEvent success(List<Event> eventList) {
        return new EventListByNameDescEvent(true, null, null, eventList);
    }

    public static EventListByNameDescEvent fail(Exception e, String errorMessage) {
        return new EventListByNameDescEvent(false, errorMessage, e, null);
    }
}
