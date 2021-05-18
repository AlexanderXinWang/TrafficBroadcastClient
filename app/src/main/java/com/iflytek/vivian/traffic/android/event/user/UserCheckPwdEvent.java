package com.iflytek.vivian.traffic.android.event.user;

import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

public class UserCheckPwdEvent extends BaseNetworkEvent {
    public UserCheckPwdEvent(boolean success, String errorMessage, Exception e, Boolean data) {
        super(success, errorMessage, e, data);
    }

    public static UserCheckPwdEvent success(Boolean data){
        return new UserCheckPwdEvent(true,null,null,data);
    }

    public static UserCheckPwdEvent fail(Exception e, String errMessage){
        return new UserCheckPwdEvent(false,errMessage,e,null);
    }
}
