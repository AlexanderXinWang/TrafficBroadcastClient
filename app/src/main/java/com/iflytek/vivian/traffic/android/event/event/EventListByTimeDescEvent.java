package com.iflytek.vivian.traffic.android.event.event;

import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

import java.util.List;

public class EventListByTimeDescEvent extends BaseNetworkEvent<List<Event>> {
    public EventListByTimeDescEvent(boolean success, String errorMessage, Exception e, List<Event> data) {
        super(success, errorMessage, e, data);
    }

    public static EventListByTimeDescEvent success(List<Event> eventList) {
        return new EventListByTimeDescEvent(true, null, null, eventList);
    }

    public static EventListByTimeDescEvent fail(Exception e, String errorMessage) {
        return new EventListByTimeDescEvent(false, errorMessage, e, null);
    }
}
