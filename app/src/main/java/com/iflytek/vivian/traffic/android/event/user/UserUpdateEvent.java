package com.iflytek.vivian.traffic.android.event.user;

import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.BaseNetworkEvent;
import com.iflytek.vivian.traffic.android.event.event.EventUpdateEvent;

public class UserUpdateEvent extends BaseNetworkEvent<User> {
    public UserUpdateEvent(boolean success, String errorMessage, Exception e, User data) {
        super(success, errorMessage, e, data);
    }

    public static UserUpdateEvent success(User user) {
        return new UserUpdateEvent(true, null, null, user);
    }

    public static UserUpdateEvent fail(Exception e, String errorMessage) {
        return new UserUpdateEvent(false, errorMessage, e, null);
    }
}
