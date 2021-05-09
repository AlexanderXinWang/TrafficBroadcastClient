package com.iflytek.vivian.traffic.android.event.user;

import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

import java.util.List;

public class UserListByIdAscEvent extends BaseNetworkEvent<List<User>> {
    public UserListByIdAscEvent(boolean success, String errorMessage, Exception e, List<User> data) {
        super(success, errorMessage, e, data);
    }

    public static UserListByIdAscEvent success(List<User> data){
        return new UserListByIdAscEvent(true,null,null,data);
    }

    public static UserListByIdAscEvent fail(Exception e, String errMessage){
        return new UserListByIdAscEvent(false,errMessage,e,null);
    }
}
