package com.iflytek.vivian.traffic.android.event;

import com.iflytek.vivian.traffic.android.dto.Search;
import com.iflytek.vivian.traffic.android.event.user.UserDeleteEvent;

public class SearchEvent extends BaseNetworkEvent<Search> {
    public SearchEvent(boolean success, String errorMessage, Exception e, Search data) {
        super(success, errorMessage, e, data);
    }

    public static SearchEvent success(Search data){
        return new SearchEvent(true,null,null,data);
    }

    public static SearchEvent  fail(Exception e,String errMessage){
        return new SearchEvent(false,errMessage,e,null);
    }
}
