package com.iflytek.vivian.traffic.android.event.user;

import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

import java.util.List;

public class UserListByIdDescEvent extends BaseNetworkEvent<List<User>> {
    public UserListByIdDescEvent(boolean success, String errorMessage, Exception e, List<User> data) {
        super(success, errorMessage, e, data);
    }

    public static UserListByIdDescEvent success(List<User> data){
        return new UserListByIdDescEvent(true,null,null,data);
    }

    public static UserListByIdDescEvent fail(Exception e, String errMessage){
        return new UserListByIdDescEvent(false,errMessage,e,null);
    }
}
