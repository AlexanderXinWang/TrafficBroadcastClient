package com.iflytek.vivian.traffic.android.event;

import com.iflytek.vivian.traffic.android.dto.Event;

public class EventDetailEvent extends BaseNetworkEvent {
    public EventDetailEvent(boolean success, String errorMessage, Exception e, Object data) {
        super(success, errorMessage, e, data);
    }

    public static EventDetailEvent success(Event event) {
        return new EventDetailEvent(true, null, null, event);
    }

    public static EventDetailEvent fail(Exception e, String errorMessage) {
        return new EventDetailEvent(false, errorMessage, e, null);
    }
}
