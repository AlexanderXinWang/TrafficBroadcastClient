package com.iflytek.vivian.traffic.android.event.user;

import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

import java.util.List;

public class UserListByNameDescEvent extends BaseNetworkEvent<List<User>> {
    public UserListByNameDescEvent(boolean success, String errorMessage, Exception e, List<User> data) {
        super(success, errorMessage, e, data);
    }

    public static UserListByNameDescEvent success(List<User> data){
        return new UserListByNameDescEvent(true,null,null,data);
    }

    public static UserListByNameDescEvent fail(Exception e, String errMessage){
        return new UserListByNameDescEvent(false,errMessage,e,null);
    }
}
