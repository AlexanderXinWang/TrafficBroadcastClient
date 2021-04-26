package com.iflytek.vivian.traffic.android.client;

import android.util.Log;

import com.iflytek.vivian.traffic.android.client.retrofit.UserService;
import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.dto.Result;
import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.event.EventDeleteEvent;
import com.iflytek.vivian.traffic.android.event.event.EventDetailEvent;
import com.iflytek.vivian.traffic.android.event.event.EventSaveEvent;
import com.iflytek.vivian.traffic.android.event.user.UserDeleteEvent;
import com.iflytek.vivian.traffic.android.event.user.UserDetailEvent;
import com.iflytek.vivian.traffic.android.event.user.UserListEvent;
import com.iflytek.vivian.traffic.android.event.user.UserLoginEvent;
import com.iflytek.vivian.traffic.android.event.user.UserSaveEvent;
import com.iflytek.vivian.traffic.android.event.user.UserUpdateEvent;
import com.iflytek.vivian.traffic.android.exception.ApiInvokeException;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.security.interfaces.RSAKey;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

/**
 * 后台服务调用类
 */
public class UserClient {

    private final static String TAG="UserClient";

    /**
     * 用户登录
     * @param serverUrl
     * @param user
     */
    public static void userLogin(String serverUrl, User user) {
        new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(FastJsonConverterFactory.create())
                .build()
                .create(UserService.class).userLogin(user).enqueue(new retrofit2.Callback<Result<User>>() {
            @Override
            public void onResponse(Call<Result<User>> call, Response<Result<User>> response) {
                try {
                    Log.i(TAG, "接口返回：" + response.message());
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

    /**
     * 新增用户
     * @param serverUrl
     * @param user
     */
    public static void saveUser(String serverUrl, User user) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(UserService.class).saveUser(user).enqueue(new Callback<Result<User>>() {
            @Override
            public void onResponse(Call<Result<User>> call, Response<Result<User>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "调用saveUser接口返回：" + response.message());
                        Result<User> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(UserSaveEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(UserSaveEvent.fail(new ApiInvokeException("saveUser接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求saveUser接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(UserSaveEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(UserSaveEvent.fail(new ApiInvokeException(e), e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<User>> call, Throwable t) {
                Log.e(TAG, "请求异常" + t.getMessage(), t);
                EventBus.getDefault().post(EventSaveEvent.fail(new ApiInvokeException(t), t.getMessage()));
            }
        });
    }

    /**
     * 删除用户（多选）
     * @param serverUrl
     * @param userIds
     */
    public static void deleteUser(String serverUrl, List<String> userIds) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(UserService.class).deleteUser(userIds).enqueue(new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(Call<Result<Boolean>> call, Response<Result<Boolean>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "调用deleteUser接口返回：" + response.message());
                        Result<Boolean> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(UserDeleteEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(UserDeleteEvent.fail(new ApiInvokeException("deleteUser接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求deleteUser接口失败" + response.errorBody().string());
                        EventBus.getDefault().post(UserDeleteEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(EventDeleteEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<Boolean>> call, Throwable t) {
                Log.e(TAG, "请求异常：" + t.getMessage(), t);
                EventBus.getDefault().post(EventDeleteEvent.fail(new ApiInvokeException(t), t.getMessage()));
            }
        });
    }

    /**
     * 更新用户信息
     * @param serverUrl
     * @param user
     */
    public static void updateUser(String serverUrl, User user) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(UserService.class).updateUser(user).enqueue(new Callback<Result<User>>() {
            @Override
            public void onResponse(Call<Result<User>> call, Response<Result<User>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "调用updateUser接口返回：" + response.message());
                        Result<User> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(UserUpdateEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(UserUpdateEvent.fail(new ApiInvokeException("updateUser接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求updateUser接口失败" + response.errorBody().string());
                        EventBus.getDefault().post(UserUpdateEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(UserUpdateEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<User>> call, Throwable t) {
                Log.e(TAG, "请求异常：" + t.getMessage());
                EventBus.getDefault().post(UserUpdateEvent.fail(new ApiInvokeException(t.getMessage()), t.getMessage()));
            }
        });
    }

    /**
     * 查询用户信息（所有）
     * @param serverUrl
     */
    public static void listUser(String serverUrl) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(UserService.class).listUser().enqueue(new Callback<Result<List<User>>>() {
            @Override
            public void onResponse(Call<Result<List<User>>> call, Response<Result<List<User>>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "调用listUser接口返回：" + response.message());
                        Result<List<User>> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(UserListEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(UserListEvent.fail(new ApiInvokeException("listUser接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求listUser接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(UserListEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(UserListEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<List<User>>> call, Throwable t) {
                Log.e(TAG, "请求异常：" + t.getMessage());
                EventBus.getDefault().post(UserListEvent.fail(new ApiInvokeException(t.getMessage()), t.getMessage()));
            }
        });
    }

    /**
     * 查询单个用户详情信息
     * @param serverUrl
     * @param userId
     */
    public static void selectUser(String serverUrl, String userId) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(UserService.class).selectUser(userId).enqueue(new Callback<Result<User>>() {
            @Override
            public void onResponse(Call<Result<User>> call, Response<Result<User>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "调用selectUser接口返回：" + response.message());
                        Result<User> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(UserDetailEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(UserDetailEvent.fail(new ApiInvokeException(result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求selectUser接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(UserDetailEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(UserDetailEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<User>> call, Throwable t) {
                Log.e(TAG, "请求异常：" + t.getMessage());
                EventBus.getDefault().post(EventDetailEvent.fail(new ApiInvokeException(t.getMessage()), t.getMessage()));
            }
        });
    }
}
