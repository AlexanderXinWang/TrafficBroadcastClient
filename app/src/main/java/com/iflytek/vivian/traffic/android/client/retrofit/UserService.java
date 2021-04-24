package com.iflytek.vivian.traffic.android.client.retrofit;

import com.iflytek.vivian.traffic.android.dto.Result;
import com.iflytek.vivian.traffic.android.dto.User;


import java.security.interfaces.RSAKey;
import java.time.format.ResolverStyle;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserService {

    @POST("/traffic-server/user/login")
    Call<Result<User>> userLogin(@Query("username") String username, @Query("password") String password);

    @POST("/traffic-server/user/save")
    Call<Result<User>> saveUser(@Body User user);

    @POST("/traffic-server/user/delete")
    Call<Result<Boolean>> deleteUser(@Query("id") String userId);

    @POST("/traffic-server/user/update")
    Call<Result<User>> updateUser(@Body User user);

    @POST("/traffic-server/user/list")
    Call<Result<List<User>>> listUser();

    @POST("/traffic-server/user/detail")
    Call<Result<User>> selectUser(@Query("id") String userId);

}
