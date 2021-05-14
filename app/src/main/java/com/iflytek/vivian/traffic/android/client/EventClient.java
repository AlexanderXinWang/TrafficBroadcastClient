package com.iflytek.vivian.traffic.android.client;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.spring.FastjsonSockJsMessageCodec;
import com.iflytek.vivian.traffic.android.client.retrofit.EventService;
import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.dto.Result;
import com.iflytek.vivian.traffic.android.event.event.EventDeleteEvent;
import com.iflytek.vivian.traffic.android.event.event.EventDetailEvent;
import com.iflytek.vivian.traffic.android.event.event.EventFindByUserIdEvent;
import com.iflytek.vivian.traffic.android.event.event.EventGetPlayPathEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListByEventAscEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListByEventDescEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListByLocationAscEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListByLocationDescEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListByNameAscEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListByNameDescEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListByTimeAscEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListByTimeDescEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListEvent;
import com.iflytek.vivian.traffic.android.event.event.EventSaveEvent;
import com.iflytek.vivian.traffic.android.event.event.EventUpdateEvent;
import com.iflytek.vivian.traffic.android.event.event.IatEvent;
import com.iflytek.vivian.traffic.android.exception.ApiInvokeException;
import com.iflytek.vivian.traffic.android.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

public class EventClient {

    private final static String TAG="EventClient";

    private static final OkHttpClient CLIENT = new OkHttpClient().newBuilder()
            .connectTimeout(100, TimeUnit.SECONDS).readTimeout(100, TimeUnit.SECONDS).build();

    /**
     * 获取未播报警情的MP3地址
     * @param serverUrl
     */
    public static void getEventPlayPath(String serverUrl) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(EventService.class).getEventPlayPath().enqueue(new Callback<Result<List<String>>>() {
            @Override
            public void onResponse(Call<Result<List<String>>> call, Response<Result<List<String>>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "调用getEventPlayPath接口返回：" + response.message());
                        Result<List<String>> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(EventGetPlayPathEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(EventGetPlayPathEvent.fail(new ApiInvokeException("getEventPlayPath接口返回失败" + result.getErrorMessage()),result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求getEventPlayPath接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(EventGetPlayPathEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(EventGetPlayPathEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<List<String>>> call, Throwable t) {
                Log.e(TAG, "请求异常：" + t.getMessage());
                EventBus.getDefault().post(EventGetPlayPathEvent.fail(new ApiInvokeException(t.getMessage()), t.getMessage()));
            }
        });
    }

    /**
     * 语音识别警情信息
     * @param serverUrl
     * @param voiceData
     */
    public static void iatEvent(String serverUrl, byte[] voiceData) {
        MultipartBody.Part file = MultipartBody.Part
                .createFormData("file", "file.pcm", RequestBody.create(MediaType.parse("audio/pcm"), voiceData));

        new Retrofit.Builder().client(CLIENT)
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(EventService.class).iatEvent(file).enqueue(new Callback<Result<Event>>() {
            @Override
            public void onResponse(Call<Result<Event>> call, Response<Result<Event>> response) {
                try {
                    Log.i(TAG, "调用iat接口返回：" + response.message());
                    if (response.isSuccessful()) {
                        Result<Event> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(IatEvent.success(result.getData()));
                            System.out.println("返回数据=" + JSON.toJSONString(result.getData()));
                        } else {
                            EventBus.getDefault().post(IatEvent.fail(new ApiInvokeException("iat接口返回失败：" + result.getErrorMessage()), "接口返回错误信息"));
                        }
                    } else {
                        EventBus.getDefault().post(IatEvent.fail(new ApiInvokeException("事件上报错误"), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    Log.e(TAG, "请求异常：" + e.getMessage());
                    EventBus.getDefault().post(IatEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Result<Event>> call, Throwable t) {
                Log.e(TAG, "iat识别失败：" + t.getMessage(), t);
                EventBus.getDefault().post(IatEvent.fail(new ApiInvokeException(t), "iat识别失败"));
            }
        });
    }

    /**
     * 新增/上报警情事件
     * @param serverUrl
     * @param event
     */
    public static void saveEvent(String serverUrl, Event event) {

        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(EventService.class).saveEvent(event).enqueue(new Callback<Result<Event>>() {
            @Override
            public void onResponse(Call<Result<Event>> call, Response<Result<Event>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "调用saveEvent接口返回：" + response.message());
                        Result<Event> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(EventSaveEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(EventSaveEvent.fail(new ApiInvokeException("saveEvent接口返回失败" + result.getErrorMessage()),result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求saveEvent接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(EventSaveEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(EventSaveEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<Event>> call, Throwable t) {
                Log.e(TAG, "请求异常：" + t.getMessage());
                EventBus.getDefault().post(EventSaveEvent.fail(new ApiInvokeException(t.getMessage()), t.getMessage()));
            }
        });
    }

    /**
     * 删除警情事件（多选）
     * @param serverUrl
     * @param eventIdList
     */
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
                    EventBus.getDefault().post(EventDeleteEvent.fail(new ApiInvokeException("deleteEvent接口返回失败：" + result.getErrorMessage()), "接口返回错误信息"));
                }
            }

            @Override
            public void onFailure(Call<Result<Boolean>> call, Throwable t) {
                Log.e(TAG, "请求deleteEvent接口失败：" + t.getMessage(), t);
                EventBus.getDefault().post(EventDeleteEvent.fail(new ApiInvokeException(t), "删除失败"));
            }
        });
    }

    /**
     * 更新警情事件信息
     * @param serverUrl
     * @param event
     */
    public static void updateEvent(String serverUrl, Event event) {

        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(EventService.class).updateEvent(event).enqueue(new Callback<Result<Event>>() {
            @Override
            public void onResponse(Call<Result<Event>> call, Response<Result<Event>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "请求updateEvent接口返回：" + response.message());
                        Result<Event> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(EventUpdateEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(EventUpdateEvent.fail(new ApiInvokeException("请求updateEvent接口返回失败："
                                    + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求updateEvent接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(EventUpdateEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(EventUpdateEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<Event>> call, Throwable t) {
                Log.e(TAG, "请求异常" + t.getMessage());
                EventBus.getDefault().post(EventUpdateEvent.fail(new ApiInvokeException(t.getMessage()), t.getMessage()));
            }
        });
    }

    /**
     * 查询警情事件（所有）
     * @param serverUrl
     */
    public static void listEvent(String serverUrl) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(EventService.class).listEvent().enqueue(new Callback<Result<List<Event>>>() {
            @Override
            public void onResponse(Call<Result<List<Event>>> call, Response<Result<List<Event>>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "listEvent接口返回：" + response.message());
                        Result<List<Event>> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(EventListEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(EventListEvent.fail(new ApiInvokeException("listEvent接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求listEvent接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(EventListEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(EventListEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<List<Event>>> call, Throwable t) {
                Log.e(TAG, "请求异常" + t.getMessage(), t);
                EventBus.getDefault().post(EventListEvent.fail(new ApiInvokeException(t), t.getMessage()));
            }
        });
    }

    /**
     * 查询单个警情事件详情信息
     * @param serverUrl
     * @param eventId
     */
    public static void selectEvent(String serverUrl, String eventId) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(EventService.class).selectEvent(eventId).enqueue(new Callback<Result<Event>>() {
            @Override
            public void onResponse(Call<Result<Event>> call, Response<Result<Event>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "调用selectEvent接口返回：" + response.message());
                        Result<Event> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(EventDetailEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(EventDetailEvent.fail(new ApiInvokeException("selectEvent接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求selectEvent接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(EventDetailEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(EventDetailEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<Event>> call, Throwable t) {
                Log.e(TAG, "请求异常：" + t.getMessage());
                EventBus.getDefault().post(EventDetailEvent.fail(new ApiInvokeException(t), t.getMessage()));
            }
        });
    }

    /**
     * 根据用户Id获取事件列表
     * @param serverUrl
     * @param userId
     */
    public static void findEventByUserId(String serverUrl, String userId) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(EventService.class).findEventByUserId(userId).enqueue(new Callback<Result<List<Event>>>() {
            @Override
            public void onResponse(Call<Result<List<Event>>> call, Response<Result<List<Event>>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "findEventByUserId接口返回：" + response.message());
                        Result<List<Event>> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(EventFindByUserIdEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(EventFindByUserIdEvent.fail(new ApiInvokeException("findEventByUserId接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求findEventByUserId接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(EventFindByUserIdEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(EventFindByUserIdEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<List<Event>>> call, Throwable t) {
                Log.e(TAG, "findEventByUserId请求异常" + t.getMessage(), t);
                EventBus.getDefault().post(EventFindByUserIdEvent.fail(new ApiInvokeException(t), t.getMessage()));
            }
        });
    }

    public static void listEventByTimeAsc(String serverUrl) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(EventService.class).listEventByTimeAsc().enqueue(new Callback<Result<List<Event>>>() {
            @Override
            public void onResponse(Call<Result<List<Event>>> call, Response<Result<List<Event>>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "listEventByTimeAsc接口返回：" + response.message());
                        Result<List<Event>> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(EventListByTimeAscEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(EventListByTimeAscEvent.fail(new ApiInvokeException("listEventByTimeAsc接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求listEventByTimeAsc接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(EventListByTimeAscEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(EventListByTimeAscEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<List<Event>>> call, Throwable t) {
                Log.e(TAG, "listEventByTimeAsc请求异常" + t.getMessage(), t);
                EventBus.getDefault().post(EventListByTimeAscEvent.fail(new ApiInvokeException(t), t.getMessage()));
            }
        });
    }

    public static void listEventByTimeDesc(String serverUrl) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(EventService.class).listEventByTimeDesc().enqueue(new Callback<Result<List<Event>>>() {
            @Override
            public void onResponse(Call<Result<List<Event>>> call, Response<Result<List<Event>>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "listEventByTimeDesc接口返回：" + response.message());
                        Result<List<Event>> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(EventListByTimeDescEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(EventListByTimeDescEvent.fail(new ApiInvokeException("listEventByTimeDesc接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求listEventByTimeDesc接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(EventListByTimeDescEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(EventListByTimeDescEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<List<Event>>> call, Throwable t) {
                Log.e(TAG, "listEventByTimeDesc请求异常" + t.getMessage(), t);
                EventBus.getDefault().post(EventListByTimeDescEvent.fail(new ApiInvokeException(t), t.getMessage()));
            }
        });
    }

    public static void listEventByEventAsc(String serverUrl) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(EventService.class).listEventByEventAsc().enqueue(new Callback<Result<List<Event>>>() {
            @Override
            public void onResponse(Call<Result<List<Event>>> call, Response<Result<List<Event>>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "listEventByTimeAsc接口返回：" + response.message());
                        Result<List<Event>> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(EventListByEventAscEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(EventListByEventAscEvent.fail(new ApiInvokeException("listEventByTimeAsc接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求listEventByTimeAsc接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(EventListByEventAscEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(EventListByEventAscEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<List<Event>>> call, Throwable t) {
                Log.e(TAG, "listEventByTimeAsc请求异常" + t.getMessage(), t);
                EventBus.getDefault().post(EventListByEventAscEvent.fail(new ApiInvokeException(t), t.getMessage()));
            }
        });
    }

    public static void listEventByEventDesc(String serverUrl) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(EventService.class).listEventByEventDesc().enqueue(new Callback<Result<List<Event>>>() {
            @Override
            public void onResponse(Call<Result<List<Event>>> call, Response<Result<List<Event>>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "listEventByTimeAsc接口返回：" + response.message());
                        Result<List<Event>> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(EventListByEventDescEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(EventListByEventDescEvent.fail(new ApiInvokeException("listEventByTimeAsc接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求listEventByTimeAsc接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(EventListByEventDescEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(EventListByEventDescEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<List<Event>>> call, Throwable t) {
                Log.e(TAG, "listEventByTimeAsc请求异常" + t.getMessage(), t);
                EventBus.getDefault().post(EventListByEventDescEvent.fail(new ApiInvokeException(t), t.getMessage()));
            }
        });
    }

    public static void listEventByNameAsc(String serverUrl) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(EventService.class).listEventByNameAsc().enqueue(new Callback<Result<List<Event>>>() {
            @Override
            public void onResponse(Call<Result<List<Event>>> call, Response<Result<List<Event>>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "listEventByNameAsc接口返回：" + response.message());
                        Result<List<Event>> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(EventListByNameAscEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(EventListByNameAscEvent.fail(new ApiInvokeException("listEventByNameAsc接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求listEventByNameAsc接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(EventListByNameAscEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(EventListByNameAscEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<List<Event>>> call, Throwable t) {
                Log.e(TAG, "listEventByNameAsc请求异常" + t.getMessage(), t);
                EventBus.getDefault().post(EventListByNameAscEvent.fail(new ApiInvokeException(t), t.getMessage()));
            }
        });
    }

    public static void listEventByNameDesc(String serverUrl) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(EventService.class).listEventByNameDesc().enqueue(new Callback<Result<List<Event>>>() {
            @Override
            public void onResponse(Call<Result<List<Event>>> call, Response<Result<List<Event>>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "listEventByNameDesc接口返回：" + response.message());
                        Result<List<Event>> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(EventListByNameDescEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(EventListByNameDescEvent.fail(new ApiInvokeException("listEventByNameDesc接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求listEventByNameDesc接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(EventListByNameDescEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(EventListByNameDescEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<List<Event>>> call, Throwable t) {
                Log.e(TAG, "listEventByNameDesc请求异常" + t.getMessage(), t);
                EventBus.getDefault().post(EventListByNameDescEvent.fail(new ApiInvokeException(t), t.getMessage()));
            }
        });
    }

    public static void listEventByLocationAsc(String serverUrl) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(EventService.class).listEventByLocationAsc().enqueue(new Callback<Result<List<Event>>>() {
            @Override
            public void onResponse(Call<Result<List<Event>>> call, Response<Result<List<Event>>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "listEventByLocationAsc接口返回：" + response.message());
                        Result<List<Event>> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(EventListByLocationAscEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(EventListByLocationAscEvent.fail(new ApiInvokeException("listEventByLocationAsc接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求listEventByLocationAsc接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(EventListByLocationAscEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(EventListByLocationAscEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<List<Event>>> call, Throwable t) {
                Log.e(TAG, "listEventByLocationAsc请求异常" + t.getMessage(), t);
                EventBus.getDefault().post(EventListByLocationAscEvent.fail(new ApiInvokeException(t), t.getMessage()));
            }
        });
    }

    public static void listEventByLocationDesc(String serverUrl) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(EventService.class).listEventByLocationDesc().enqueue(new Callback<Result<List<Event>>>() {
            @Override
            public void onResponse(Call<Result<List<Event>>> call, Response<Result<List<Event>>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "listEventByLocationDesc接口返回：" + response.message());
                        Result<List<Event>> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(EventListByLocationDescEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(EventListByLocationDescEvent.fail(new ApiInvokeException("listEventByLocationDesc接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求listEventByLocationDesc接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(EventListByLocationDescEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(EventListByLocationDescEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<List<Event>>> call, Throwable t) {
                Log.e(TAG, "listEventByLocationDesc请求异常" + t.getMessage(), t);
                EventBus.getDefault().post(EventListByLocationDescEvent.fail(new ApiInvokeException(t), t.getMessage()));
            }
        });
    }
}
