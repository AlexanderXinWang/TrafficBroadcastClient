package com.iflytek.vivian.traffic.android.event.event;

import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

public class EventUpdateEvent extends BaseNetworkEvent {

    public EventUpdateEvent(boolean success, String errorMessage, Exception e, Object data) {
        super(success, errorMessage, e, data);
    }

    public static EventUpdateEvent success(Event event) {
        return new EventUpdateEvent(true, null, null, event);
    }

    public static EventUpdateEvent fail(Exception e, String errorMessage) {
        return new EventUpdateEvent(false, errorMessage, e, null);
    }
}
