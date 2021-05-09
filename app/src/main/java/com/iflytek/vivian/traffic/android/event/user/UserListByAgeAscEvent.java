package com.iflytek.vivian.traffic.android.event.user;

import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

import java.util.List;

public class UserListByAgeAscEvent extends BaseNetworkEvent<List<User>> {
    public UserListByAgeAscEvent(boolean success, String errorMessage, Exception e, List<User> data) {
        super(success, errorMessage, e, data);
    }

    public static UserListByAgeAscEvent success(List<User> data){
        return new UserListByAgeAscEvent(true,null,null,data);
    }

    public static UserListByAgeAscEvent fail(Exception e, String errMessage){
        return new UserListByAgeAscEvent(false,errMessage,e,null);
    }
}
