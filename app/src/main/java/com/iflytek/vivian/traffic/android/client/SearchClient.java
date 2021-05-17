package com.iflytek.vivian.traffic.android.client;

import android.util.Log;

import com.iflytek.vivian.traffic.android.client.retrofit.SearchService;
import com.iflytek.vivian.traffic.android.dto.Result;
import com.iflytek.vivian.traffic.android.dto.Search;
import com.iflytek.vivian.traffic.android.event.SearchEvent;
import com.iflytek.vivian.traffic.android.event.event.EventGetPlayPathEvent;
import com.iflytek.vivian.traffic.android.exception.ApiInvokeException;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

public class SearchClient {
    private final static String TAG="SearchClient";

    public static void search(String serverUrl, String query) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(SearchService.class).search(query).enqueue(new Callback<Result<Search>>() {
            @Override
            public void onResponse(Call<Result<Search>> call, Response<Result<Search>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "调用SearchEvent接口返回：" + response.message());
                        Result<Search> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(SearchEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(SearchEvent.fail(new ApiInvokeException("SearchEvent接口返回失败" + result.getErrorMessage()),result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求SearchEvent接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(SearchEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(SearchEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<Search>> call, Throwable t) {
                Log.e(TAG, "请求异常：" + t.getMessage());
                EventBus.getDefault().post(SearchEvent.fail(new ApiInvokeException(t.getMessage()), t.getMessage()));
            }
        });
    }
}
