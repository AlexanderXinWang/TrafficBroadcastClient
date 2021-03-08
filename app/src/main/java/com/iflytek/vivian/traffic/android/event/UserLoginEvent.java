package com.iflytek.vivian.traffic.android.event;

import com.iflytek.vivian.traffic.android.dto.User;

public class UserLoginEvent extends BaseNetworkEvent<User> {
    public UserLoginEvent(boolean success, String errorMessage, Exception e, User data) {
        super(success, errorMessage, e, data);
    }

    public static UserLoginEvent success(User data){
        return new UserLoginEvent(true,null,null,data);
    }

    public static UserLoginEvent fail(Exception e,String errMessage){
        return new UserLoginEvent(false,errMessage,e,null);
    }
}
