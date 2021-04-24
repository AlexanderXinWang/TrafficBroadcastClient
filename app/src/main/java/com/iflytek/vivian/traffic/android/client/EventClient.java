package com.iflytek.vivian.traffic.android.client;

import android.provider.CalendarContract;
import android.renderscript.RenderScript;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.iflytek.vivian.traffic.android.client.retrofit.EventService;
import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.dto.Result;
import com.iflytek.vivian.traffic.android.event.EventDeleteEvent;
import com.iflytek.vivian.traffic.android.event.EventDetailEvent;
import com.iflytek.vivian.traffic.android.event.EventListEvent;
import com.iflytek.vivian.traffic.android.event.EventRefreshEvent;
import com.iflytek.vivian.traffic.android.event.EventSaveEvent;
import com.iflytek.vivian.traffic.android.event.EventUpdateEvent;
import com.iflytek.vivian.traffic.android.event.IatEvent;
import com.iflytek.vivian.traffic.android.exception.ApiInvokeException;
import com.umeng.commonsdk.debug.E;

import org.greenrobot.eventbus.EventBus;

import java.io.IOError;
import java.io.IOException;
import java.security.interfaces.RSAKey;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

public class EventClient {

    private final static String TAG="EventClient";

    public static void iatEvent(String serverUrl, byte[] voiceData) {
        MultipartBody.Part file = MultipartBody.Part
                .createFormData("file", "file.pcm", RequestBody.create(MediaType.parse("audio/pcm"), voiceData));

        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(EventService.class).iatEvent(file).enqueue(new Callback<Result<Event>>() {
            @Override
            public void onResponse(Call<Result<Event>> call, Response<Result<Event>> response) {
                try {
                    Log.i(TAG, "调用iat识别接口成功。");
                    if (response.isSuccessful()) {
                        Result<Event> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(IatEvent.success(result.getData()));
                            System.out.println("返回数据=" + JSON.toJSONString(result.getData()));
                        } else {
                            EventBus.getDefault().post(IatEvent.fail(new ApiInvokeException("接口返回失败：" + result.getErrorMessage()), "接口返回错误信息"));
                        }
                    } else {
                        EventBus.getDefault().post(IatEvent.fail(new ApiInvokeException("事件上报错误"), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(IatEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Result<Event>> call, Throwable t) {
                Log.e(TAG, "iat识别失败：" + t.getMessage(), t);
                EventBus.getDefault().post(IatEvent.fail(new Exception(t), "iat识别失败"));
            }
        });
    }

    public static void saveEvent(String serverUrl, Event event) {

        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(EventService.class).saveEvent(event).enqueue(new Callback<Result<Event>>() {
            @Override
            public void onResponse(Call<Result<Event>> call, Response<Result<Event>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "接口返回：" + response.message());
                        Result<Event> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(EventSaveEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(EventSaveEvent.fail(new ApiInvokeException(result.getErrorMessage()),result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求saveEvent接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(EventSaveEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(EventSaveEvent.fail(new ApiInvokeException(e), e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<Event>> call, Throwable t) {
                Log.e(TAG, "请求异常：" + t.getMessage());
                EventBus.getDefault().post(EventSaveEvent.fail(new ApiInvokeException(t.getMessage()), t.getMessage()));
            }
        });
    }

    public static void deleteEvent(String serverUrl, List<String> eventIdList) {

        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(EventService.class).deleteEvent(eventIdList).enqueue(new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(Call<Result<Boolean>> call, Response<Result<Boolean>> response) {
                Log.i(TAG, "调用deleteEvent接口成功");
                Result<Boolean> result = response.body();
                if (result.isSuccess()) {
                    EventBus.getDefault().post(EventDeleteEvent.success(result.getData()));
                } else {
                    EventBus.getDefault().post(EventDeleteEvent.fail(new ApiInvokeException("接口返回失败：" + result.getErrorMessage()), "接口返回错误信息"));
                }
            }

            @Override
            public void onFailure(Call<Result<Boolean>> call, Throwable t) {
                Log.e(TAG, "删除失败：" + t.getMessage(), t);
                EventBus.getDefault().post(EventDeleteEvent.fail(new Exception(t), "删除失败"));
            }
        });
    }

    public static void updateEvent(String serverUrl, Event event) {

        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(EventService.class).updateEvent(event).enqueue(new Callback<Result<Event>>() {
            @Override
            public void onResponse(Call<Result<Event>> call, Response<Result<Event>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "接口返回：" + response.message());
                        Result<Event> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(EventUpdateEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(EventUpdateEvent.fail(new ApiInvokeException(result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求updateEvent接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(EventUpdateEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(EventUpdateEvent.fail(new ApiInvokeException(e), e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<Event>> call, Throwable t) {
                Log.e(TAG, "请求异常" + t.getMessage());
                EventBus.getDefault().post(EventUpdateEvent.fail(new ApiInvokeException(t.getMessage()), t.getMessage()));
            }
        });
    }

    public static void listEvent(String serverUrl) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(EventService.class).listEvent().enqueue(new Callback<Result<List<Event>>>() {
            @Override
            public void onResponse(Call<Result<List<Event>>> call, Response<Result<List<Event>>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "接口返回：" + response.message());
                        Result<List<Event>> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(EventListEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(EventListEvent.fail(new ApiInvokeException(result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求listEvent接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(EventListEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(EventListEvent.fail(new ApiInvokeException(e), e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<List<Event>>> call, Throwable t) {

            }
        });
    }

    public static void selectEvent(String serverUrl, String eventId) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(EventService.class).selectEvent(eventId).enqueue(new Callback<Result<Event>>() {
            @Override
            public void onResponse(Call<Result<Event>> call, Response<Result<Event>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "接口返回：" + response.message());
                        Result<Event> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(EventDetailEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(EventDetailEvent.fail(new ApiInvokeException(result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求selectEvent接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(EventDetailEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(EventDetailEvent.fail(new ApiInvokeException(e), e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<Event>> call, Throwable t) {
                Log.e(TAG, "请求异常：" + t.getMessage());
                EventBus.getDefault().post(EventDetailEvent.fail(new ApiInvokeException(t), t.getMessage()));
            }
        });
    }
}
