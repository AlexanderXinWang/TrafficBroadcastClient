package com.iflytek.vivian.traffic.android.event.user;

import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

import java.util.List;

public class UserListByNameAscEvent extends BaseNetworkEvent<List<User>> {
    public UserListByNameAscEvent(boolean success, String errorMessage, Exception e, List<User> data) {
        super(success, errorMessage, e, data);
    }

    public static UserListByNameAscEvent success(List<User> data){
        return new UserListByNameAscEvent(true,null,null,data);
    }

    public static UserListByNameAscEvent fail(Exception e, String errMessage){
        return new UserListByNameAscEvent(false,errMessage,e,null);
    }
}
