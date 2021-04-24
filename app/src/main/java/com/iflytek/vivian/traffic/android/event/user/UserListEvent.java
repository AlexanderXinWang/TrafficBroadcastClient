package com.iflytek.vivian.traffic.android.event.user;

import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

import java.util.List;

public class UserListEvent extends BaseNetworkEvent<List<User>> {
    public UserListEvent(boolean success, String errorMessage, Exception e, List<User> data) {
        super(success, errorMessage, e, data);
    }

    public static UserListEvent success(List<User> data){
        return new UserListEvent(true,null,null,data);
    }

    public static UserListEvent fail(Exception e,String errMessage){
        return new UserListEvent(false,errMessage,e,null);
    }
}
