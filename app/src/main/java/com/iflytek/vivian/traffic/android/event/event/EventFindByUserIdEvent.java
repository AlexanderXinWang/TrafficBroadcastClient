package com.iflytek.vivian.traffic.android.event.event;

import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

import java.util.List;

public class EventFindByUserIdEvent extends BaseNetworkEvent<List<Event>> {
    public EventFindByUserIdEvent(boolean success, String errorMessage, Exception e, List<Event> data) {
        super(success, errorMessage, e, data);
    }

    public static EventFindByUserIdEvent success(List<Event> eventList) {
        return new EventFindByUserIdEvent(true, null, null, eventList);
    }

    public static EventFindByUserIdEvent fail(Exception e, String errorMessage) {
        return new EventFindByUserIdEvent(false, errorMessage, e, null);
    }
}
