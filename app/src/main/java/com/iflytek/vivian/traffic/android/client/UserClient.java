package com.iflytek.vivian.traffic.android.client;

import android.util.Log;

import com.iflytek.vivian.traffic.android.client.retrofit.UserService;
import com.iflytek.vivian.traffic.android.dto.Result;
import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.event.EventDeleteEvent;
import com.iflytek.vivian.traffic.android.event.event.EventDetailEvent;
import com.iflytek.vivian.traffic.android.event.event.EventSaveEvent;
import com.iflytek.vivian.traffic.android.event.event.GetUserImageEvent;
import com.iflytek.vivian.traffic.android.event.user.UserCheckPwdEvent;
import com.iflytek.vivian.traffic.android.event.user.UserUpdateImageEvent;
import com.iflytek.vivian.traffic.android.event.user.UserUpdatePwdEvent;
import com.iflytek.vivian.traffic.android.event.user.UserUploadImageEvent;
import com.iflytek.vivian.traffic.android.event.user.UserDeleteEvent;
import com.iflytek.vivian.traffic.android.event.user.UserDetailEvent;
import com.iflytek.vivian.traffic.android.event.user.UserListByAgeAscEvent;
import com.iflytek.vivian.traffic.android.event.user.UserListByAgeDescEvent;
import com.iflytek.vivian.traffic.android.event.user.UserListByIdAscEvent;
import com.iflytek.vivian.traffic.android.event.user.UserListByIdDescEvent;
import com.iflytek.vivian.traffic.android.event.user.UserListByNameAscEvent;
import com.iflytek.vivian.traffic.android.event.user.UserListByNameDescEvent;
import com.iflytek.vivian.traffic.android.event.user.UserListEvent;
import com.iflytek.vivian.traffic.android.event.user.UserLoginEvent;
import com.iflytek.vivian.traffic.android.event.user.UserSaveEvent;
import com.iflytek.vivian.traffic.android.event.user.UserUpdateEvent;
import com.iflytek.vivian.traffic.android.exception.ApiInvokeException;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

    /**
     * 上传头像
     * @param serverUrl
     * @param file
     * @param userId
     */
    public static void uploadImage(String serverUrl, File file, String userId) {
        MultipartBody.Part image = MultipartBody.Part.createFormData("image", userId + ".jpg",
                RequestBody.create(MediaType.parse("image/jpg"), file));

        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(UserService.class).uploadImage(image, userId).enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "调用uploadImage接口返回：" + response.message());
                        Result<String> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(UserUploadImageEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(UserUploadImageEvent.fail(new ApiInvokeException("uploadImage接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求uploadImage接口失败" + response.errorBody().string());
                        EventBus.getDefault().post(UserUploadImageEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(UserUploadImageEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                Log.e(TAG, "请求异常：" + t.getMessage(), t);
                EventBus.getDefault().post(UserUploadImageEvent.fail(new ApiInvokeException(t), t.getMessage()));
            }
        });
    }

    /**
     * 根据用户Id获取其头像路径
     * @param serverUrl
     * @param userId
     */
    public static void getUserImage(String serverUrl, String userId) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(UserService.class).getUserImage(userId).enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "调用getUserImage接口返回：" + response.message());
                        Result<String> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(GetUserImageEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(GetUserImageEvent.fail(new ApiInvokeException("getUserImage接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求getUserImage接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(GetUserImageEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(GetUserImageEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                Log.e(TAG, "请求异常：" + t.getMessage());
                EventBus.getDefault().post(GetUserImageEvent.fail(new ApiInvokeException(t.getMessage()), t.getMessage()));
            }
        });
    }

    /**
     * 修改用户头像
     * @param serverUrl
     * @param user
     */
    public static void updateUserImage(String serverUrl, User user) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(UserService.class).updateUserImage(user).enqueue(new Callback<Result<User>>() {
            @Override
            public void onResponse(Call<Result<User>> call, Response<Result<User>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "调用changeUserImage接口返回：" + response.message());
                        Result<User> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(UserUpdateImageEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(UserUpdateImageEvent.fail(new ApiInvokeException("changeUserImage接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求changeUserImage接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(UserUpdateImageEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(UserUpdateImageEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<User>> call, Throwable t) {
                Log.e(TAG, "请求异常：" + t.getMessage());
                EventBus.getDefault().post(GetUserImageEvent.fail(new ApiInvokeException(t.getMessage()), t.getMessage()));
            }
        });
    }

    /**
     * 检验旧密码
     * @param serverUrl
     * @param user
     */
    public static void checkOldPassword(String serverUrl, User user) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(UserService.class).checkOldPassword(user).enqueue(new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(Call<Result<Boolean>> call, Response<Result<Boolean>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "调用checkOldPassword接口返回：" + response.message());
                        Result<Boolean> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(UserCheckPwdEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(UserCheckPwdEvent.fail(new ApiInvokeException("checkOldPassword接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求checkOldPassword接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(UserCheckPwdEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(UserCheckPwdEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<Boolean>> call, Throwable t) {
                Log.e(TAG, "请求异常：" + t.getMessage());
                EventBus.getDefault().post(UserCheckPwdEvent.fail(new ApiInvokeException(t.getMessage()), t.getMessage()));
            }
        });
    }

    public static void updatePassword(String serverUrl, User user) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(UserService.class).updatePassword(user).enqueue(new Callback<Result<Boolean>>() {
            @Override
            public void onResponse(Call<Result<Boolean>> call, Response<Result<Boolean>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "调用updatePassword接口返回：" + response.message());
                        Result<Boolean> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(UserUpdatePwdEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(UserUpdatePwdEvent.fail(new ApiInvokeException("updatePassword接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求updatePassword接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(UserUpdatePwdEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(UserUpdatePwdEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<Boolean>> call, Throwable t) {
                Log.e(TAG, "请求异常：" + t.getMessage());
                EventBus.getDefault().post(UserUpdatePwdEvent.fail(new ApiInvokeException(t.getMessage()), t.getMessage()));
            }
        });
    }

    public static void listUserByNameAsc(String serverUrl) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(UserService.class).listUserByNameAsc().enqueue(new Callback<Result<List<User>>>() {
            @Override
            public void onResponse(Call<Result<List<User>>> call, Response<Result<List<User>>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "调用listUserByNameAsc接口返回：" + response.message());
                        Result<List<User>> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(UserListByNameAscEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(UserListByNameAscEvent.fail(new ApiInvokeException("listUserByNameAsc接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求listUserByNameAsc接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(UserListByNameAscEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(UserListByNameAscEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<List<User>>> call, Throwable t) {
                Log.e(TAG, "请求异常：" + t.getMessage());
                EventBus.getDefault().post(UserListByNameAscEvent.fail(new ApiInvokeException(t.getMessage()), t.getMessage()));
            }
        });
    }

    public static void listUserByNameDesc(String serverUrl) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(UserService.class).listUserByNameDesc().enqueue(new Callback<Result<List<User>>>() {
            @Override
            public void onResponse(Call<Result<List<User>>> call, Response<Result<List<User>>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "调用listUserByNameDesc接口返回：" + response.message());
                        Result<List<User>> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(UserListByNameDescEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(UserListByNameDescEvent.fail(new ApiInvokeException("listUserByNameDesc接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求listUserByNameDesc接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(UserListByNameDescEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(UserListByNameDescEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<List<User>>> call, Throwable t) {
                Log.e(TAG, "请求异常：" + t.getMessage());
                EventBus.getDefault().post(UserListByNameDescEvent.fail(new ApiInvokeException(t.getMessage()), t.getMessage()));
            }
        });
    }

    public static void listUserByIdAsc(String serverUrl) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(UserService.class).listUserByIdAsc().enqueue(new Callback<Result<List<User>>>() {
            @Override
            public void onResponse(Call<Result<List<User>>> call, Response<Result<List<User>>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "调用listUserByIdAsc接口返回：" + response.message());
                        Result<List<User>> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(UserListByIdAscEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(UserListByIdAscEvent.fail(new ApiInvokeException("listUserByIdAsc接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求listUserByIdAsc接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(UserListByIdAscEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(UserListByIdAscEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<List<User>>> call, Throwable t) {
                Log.e(TAG, "请求异常：" + t.getMessage());
                EventBus.getDefault().post(UserListByIdAscEvent.fail(new ApiInvokeException(t.getMessage()), t.getMessage()));
            }
        });
    }

    public static void listUserByIdDesc(String serverUrl) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(UserService.class).listUserByIdDesc().enqueue(new Callback<Result<List<User>>>() {
            @Override
            public void onResponse(Call<Result<List<User>>> call, Response<Result<List<User>>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "调用listUserByIdDesc接口返回：" + response.message());
                        Result<List<User>> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(UserListByIdDescEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(UserListByIdDescEvent.fail(new ApiInvokeException("listUserByIdDesc接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求listUserByIdDesc接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(UserListByIdDescEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(UserListByIdDescEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<List<User>>> call, Throwable t) {
                Log.e(TAG, "请求异常：" + t.getMessage());
                EventBus.getDefault().post(UserListByIdDescEvent.fail(new ApiInvokeException(t.getMessage()), t.getMessage()));
            }
        });
    }

    public static void listUserByAgeAsc(String serverUrl) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(UserService.class).listUserByAgeAsc().enqueue(new Callback<Result<List<User>>>() {
            @Override
            public void onResponse(Call<Result<List<User>>> call, Response<Result<List<User>>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "调用listUserByAgeAsc接口返回：" + response.message());
                        Result<List<User>> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(UserListByAgeAscEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(UserListByAgeAscEvent.fail(new ApiInvokeException("listUserByAgeAsc接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求listUserByAgeAsc接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(UserListByAgeAscEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(UserListByAgeAscEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<List<User>>> call, Throwable t) {
                Log.e(TAG, "请求异常：" + t.getMessage());
                EventBus.getDefault().post(UserListByAgeAscEvent.fail(new ApiInvokeException(t.getMessage()), t.getMessage()));
            }
        });
    }

    public static void listUserByAgeDesc(String serverUrl) {
        new Retrofit.Builder()
                .baseUrl(serverUrl).addConverterFactory(FastJsonConverterFactory.create()).build()
                .create(UserService.class).listUserByAgeDesc().enqueue(new Callback<Result<List<User>>>() {
            @Override
            public void onResponse(Call<Result<List<User>>> call, Response<Result<List<User>>> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "调用listUserByAgeDesc接口返回：" + response.message());
                        Result<List<User>> result = response.body();
                        if (result.isSuccess()) {
                            EventBus.getDefault().post(UserListByAgeDescEvent.success(result.getData()));
                        } else {
                            EventBus.getDefault().post(UserListByAgeDescEvent.fail(new ApiInvokeException("listUserByAgeDesc接口返回失败：" + result.getErrorMessage()), result.getErrorMessage()));
                        }
                    } else {
                        Log.e(TAG, "请求listUserByAgeDesc接口失败：" + response.errorBody().string());
                        EventBus.getDefault().post(UserListByAgeDescEvent.fail(new ApiInvokeException(response.errorBody().string()), response.errorBody().string()));
                    }
                } catch (IOException e) {
                    EventBus.getDefault().post(UserListByAgeDescEvent.fail(e, e.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<Result<List<User>>> call, Throwable t) {
                Log.e(TAG, "请求异常：" + t.getMessage());
                EventBus.getDefault().post(UserListByAgeDescEvent.fail(new ApiInvokeException(t.getMessage()), t.getMessage()));
            }
        });
    }
}
