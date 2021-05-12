package com.iflytek.vivian.traffic.android.event.user;

import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

public class UserUploadImageEvent extends BaseNetworkEvent<String> {
    public UserUploadImageEvent(boolean success, String errorMessage, Exception e, String data) {
        super(success, errorMessage, e, data);
    }

    public static UserUploadImageEvent success(String data){
        return new UserUploadImageEvent(true,null,null,data);
    }

    public static UserUploadImageEvent fail(Exception e, String errMessage){
        return new UserUploadImageEvent(false,errMessage,e,null);
    }
}
