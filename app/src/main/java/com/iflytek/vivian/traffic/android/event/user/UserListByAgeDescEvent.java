package com.iflytek.vivian.traffic.android.event.user;

import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

import java.util.List;

public class UserListByAgeDescEvent extends BaseNetworkEvent<List<User>> {
    public UserListByAgeDescEvent(boolean success, String errorMessage, Exception e, List<User> data) {
        super(success, errorMessage, e, data);
    }

    public static UserListByAgeDescEvent success(List<User> data){
        return new UserListByAgeDescEvent(true,null,null,data);
    }

    public static UserListByAgeDescEvent fail(Exception e, String errMessage){
        return new UserListByAgeDescEvent(false,errMessage,e,null);
    }
}
