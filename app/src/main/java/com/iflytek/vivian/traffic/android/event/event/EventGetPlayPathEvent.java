package com.iflytek.vivian.traffic.android.event.event;

import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

import java.util.List;

public class EventGetPlayPathEvent extends BaseNetworkEvent<List<String>> {
    public EventGetPlayPathEvent(boolean success, String errorMessage, Exception e, List<String> data) {
        super(success, errorMessage, e, data);
    }

    public static EventGetPlayPathEvent success(List<String> data){
        return new EventGetPlayPathEvent(true,null,null,data);
    }

    public static EventGetPlayPathEvent fail(Exception e, String errMessage){
        return new EventGetPlayPathEvent(false, errMessage, e,null);
    }
}
