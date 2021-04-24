package com.iflytek.vivian.traffic.android.event.event;

import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

public class EventSaveEvent extends BaseNetworkEvent<Event> {
    public EventSaveEvent(boolean success, String errorMessage, Exception e, Event data) {
        super(success, errorMessage, e, data);
    }
    public static EventSaveEvent success(Event data){
        return new EventSaveEvent(true,null,null,data);
    }

    public static EventSaveEvent  fail(Exception e,String errMessage){
        return new EventSaveEvent(false, errMessage, e,null);
    }
}
