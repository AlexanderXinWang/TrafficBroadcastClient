package com.iflytek.vivian.traffic.android.event.event;

import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

import java.util.List;

public class EventListByTimeAscEvent extends BaseNetworkEvent<List<Event>> {
    public EventListByTimeAscEvent(boolean success, String errorMessage, Exception e, List<Event> data) {
        super(success, errorMessage, e, data);
    }

    public static EventListByTimeAscEvent success(List<Event> eventList) {
        return new EventListByTimeAscEvent(true, null, null, eventList);
    }

    public static EventListByTimeAscEvent fail(Exception e, String errorMessage) {
        return new EventListByTimeAscEvent(false, errorMessage, e, null);
    }
}
