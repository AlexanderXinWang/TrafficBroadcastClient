package com.iflytek.vivian.traffic.android.event.event;

import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;

public class GetUserImageEvent extends BaseNetworkEvent<String> {
    public GetUserImageEvent(boolean success, String errorMessage, Exception e, String data) {
        super(success, errorMessage, e, data);
    }

    public static GetUserImageEvent success(String data){
        return new GetUserImageEvent(true,null,null,data);
    }

    public static GetUserImageEvent fail(Exception e, String errMessage){
        return new GetUserImageEvent(false,errMessage,e,null);
    }
}
