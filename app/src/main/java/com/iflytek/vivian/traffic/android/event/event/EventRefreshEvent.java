package com.iflytek.vivian.traffic.android.event.event;

import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

import java.util.List;

public class EventRefreshEvent extends BaseNetworkEvent<List<Event>> {
    public EventRefreshEvent(boolean success, String errorMessage, Exception e, List<Event> data) {
        super(success, errorMessage, e, data);
    }

    public static EventRefreshEvent success(List<Event> eventList) {
        return new EventRefreshEvent(true,null,null,eventList);
    }

    public static EventRefreshEvent fail(Exception e, String errMessage) {
        return new EventRefreshEvent(false,errMessage,e,null);
    }
}
