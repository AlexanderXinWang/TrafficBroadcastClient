package com.iflytek.vivian.traffic.android.event.user;

import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

public class UserDetailEvent extends BaseNetworkEvent<User> {
    public UserDetailEvent(boolean success, String errorMessage, Exception e, User data) {
        super(success, errorMessage, e, data);
    }

    public static UserDetailEvent success(User data){
        return new UserDetailEvent(true,null,null,data);
    }

    public static UserDetailEvent fail(Exception e,String errMessage){
        return new UserDetailEvent(false,errMessage,e,null);
    }
}
