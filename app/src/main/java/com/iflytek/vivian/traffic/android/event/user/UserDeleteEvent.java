package com.iflytek.vivian.traffic.android.event.user;

import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

public class UserDeleteEvent extends BaseNetworkEvent {
    public UserDeleteEvent(boolean success, String errorMessage, Exception e, Boolean data) {
        super(success, errorMessage, e, data);
    }

    public static UserDeleteEvent success(Boolean data){
        return new UserDeleteEvent(true,null,null,data);
    }

    public static UserDeleteEvent  fail(Exception e,String errMessage){
        return new UserDeleteEvent(false,errMessage,e,null);
    }
}
