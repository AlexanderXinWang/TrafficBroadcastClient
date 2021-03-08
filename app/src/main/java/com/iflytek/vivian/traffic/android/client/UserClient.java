package com.iflytek.vivian.traffic.android.client;

import android.util.Log;

import com.iflytek.vivian.traffic.android.client.retrofit.UserService;
import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.dto.Result;
import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.UserLoginEvent;
import com.iflytek.vivian.traffic.android.exception.ApiInvokeException;
import com.iflytek.vivian.traffic.android.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

/**
 * 后台服务调用类
 */
public class UserClient {

    private final static String TAG="UserClient";

    public static void userLogin(String serverUrl, String username, String password) {
        new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(FastJsonConverterFactory.create())
                .build()
                .create(UserService.class).userLogin(username,password).enqueue(new retrofit2.Callback<Result<User>>() {
            @Override
            public void onResponse(Call<Result<User>> call, Response<Result<User>> response) {
                try {
                    Log.i(TAG, "调用登录接口成功");
                    if (response.isSuccessful()) {
                        Result<User> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(UserLoginEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(UserLoginEvent.fail(new ApiInvokeException("接口返回失败:" + result.getErrorMessage()), "接口返回错误信息"));
                        }
                    } else {
                        EventBus.getDefault().post(UserLoginEvent.fail(new ApiInvokeException("登陆错误"),response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(UserLoginEvent.fail(e,e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<User>> call, Throwable e) {
                Log.e(TAG, "登陆失败:" + e.getMessage(), e);
                EventBus.getDefault().post(UserLoginEvent.fail(new Exception(e),"登陆失败"));
            }
        });
    }
}
