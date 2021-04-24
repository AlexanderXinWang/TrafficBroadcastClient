package com.iflytek.vivian.traffic.android.event;

public class EventDeleteEvent extends BaseNetworkEvent {
    public EventDeleteEvent(boolean success, String errorMessage, Exception e, Boolean data) {
        super(success, errorMessage, e, data);
    }

    public static EventDeleteEvent success(Boolean data){
        return new EventDeleteEvent(true,null,null,data);
    }

    public static EventDeleteEvent  fail(Exception e,String errMessage){
        return new EventDeleteEvent(false, errMessage, e,null);
    }
}
