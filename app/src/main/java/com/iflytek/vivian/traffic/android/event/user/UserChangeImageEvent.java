package com.iflytek.vivian.traffic.android.event.user;

import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

public class UserChangeImageEvent extends BaseNetworkEvent<User> {
    public UserChangeImageEvent(boolean success, String errorMessage, Exception e, User data) {
        super(success, errorMessage, e, data);
    }

    public static UserChangeImageEvent success(User data){
        return new UserChangeImageEvent(true,null,null,data);
    }

    public static UserChangeImageEvent fail(Exception e, String errMessage){
        return new UserChangeImageEvent(false,errMessage,e,null);
    }
}
