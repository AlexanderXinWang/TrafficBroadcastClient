package com.iflytek.vivian.traffic.android.event.user;

import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

public class UserUpdateImageEvent extends BaseNetworkEvent<User> {
    public UserUpdateImageEvent(boolean success, String errorMessage, Exception e, User data) {
        super(success, errorMessage, e, data);
    }

    public static UserUpdateImageEvent success(User data){
        return new UserUpdateImageEvent(true,null,null,data);
    }

    public static UserUpdateImageEvent fail(Exception e, String errMessage){
        return new UserUpdateImageEvent(false,errMessage,e,null);
    }
}
