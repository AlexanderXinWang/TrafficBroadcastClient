package com.iflytek.vivian.traffic.android.event.user;

import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

public class UserUpdatePwdEvent extends BaseNetworkEvent {
    public UserUpdatePwdEvent(boolean success, String errorMessage, Exception e, Boolean data) {
        super(success, errorMessage, e, data);
    }

    public static UserUpdatePwdEvent success(Boolean data){
        return new UserUpdatePwdEvent(true,null,null,data);
    }

    public static UserUpdatePwdEvent fail(Exception e, String errMessage){
        return new UserUpdatePwdEvent(false,errMessage,e,null);
    }
}
