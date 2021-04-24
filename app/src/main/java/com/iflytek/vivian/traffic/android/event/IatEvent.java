package com.iflytek.vivian.traffic.android.event;

import com.iflytek.vivian.traffic.android.dto.Event;

/**
 * 语音识别上报警情事件
 */
public class IatEvent extends BaseNetworkEvent<Event> {

    public IatEvent(boolean success, String errorMessage, Exception e, Event data) {
        super(success, errorMessage, e, data);
    }

    public static IatEvent success(Event event) {
        return new IatEvent(true, null, null, event);
    }

    public static IatEvent fail(Exception e, String errorMessage) {
        return new IatEvent(false, errorMessage, e, null);
    }
}
