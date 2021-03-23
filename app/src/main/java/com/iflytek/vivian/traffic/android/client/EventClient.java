package com.iflytek.vivian.traffic.android.client;

import android.util.Log;

import com.iflytek.vivian.traffic.android.client.retrofit.EventService;
import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.dto.Result;
import com.iflytek.vivian.traffic.android.event.EventRefreshEvent;
import com.iflytek.vivian.traffic.android.exception.ApiInvokeException;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

public class EventClient {

    private final static String TAG="EventClient";

    /**
     * 刷新事件列表
     */
    public static void asyncRefreshEvent(String serverUrl) {
        new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(EventService.class).listEvent()
                .enqueue(new retrofit2.Callback<Result<List<Event>>>() {
            @Override
            public void onResponse(Call<Result<List<Event>>> call, Response<Result<List<Event>>> response) {
                Log.i(TAG, "调用刷新接口成功");
                Result<List<Event>> result = response.body();
                if (result.isSuccess()) {
                    EventBus.getDefault().post(EventRefreshEvent.success(result.getData()));
                } else {
                    EventBus.getDefault().post(EventRefreshEvent.fail(new ApiInvokeException("接口返回失败：" + result.getErrorMessage()), "接口返回错误信息"));
                }
            }

            @Override
            public void onFailure(Call<Result<List<Event>>> call, Throwable t) {
                Log.e(TAG, "刷新失败:" + t.getMessage(), t);
                EventBus.getDefault().post(EventRefreshEvent.fail(new Exception(t),"刷新失败"));
            }
        });
    }
}
