package com.iflytek.vivian.traffic.android.event.user;

import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

public class UploadImageEvent extends BaseNetworkEvent<String> {
    public UploadImageEvent(boolean success, String errorMessage, Exception e, String data) {
        super(success, errorMessage, e, data);
    }

    public static UploadImageEvent success(String data){
        return new UploadImageEvent(true,null,null,data);
    }

    public static UploadImageEvent  fail(Exception e,String errMessage){
        return new UploadImageEvent(false,errMessage,e,null);
    }
}
