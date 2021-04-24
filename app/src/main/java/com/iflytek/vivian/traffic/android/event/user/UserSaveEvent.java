package com.iflytek.vivian.traffic.android.event.user;

import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

public class UserSaveEvent extends BaseNetworkEvent<User> {
    public UserSaveEvent(boolean success, String errorMessage, Exception e, User data) {
        super(success, errorMessage, e, data);
    }

    public static UserSaveEvent success(User data){
        return new UserSaveEvent(true,null,null,data);
    }

    public static UserSaveEvent fail(Exception e,String errMessage){
        return new UserSaveEvent(false,errMessage,e,null);
    }
}
