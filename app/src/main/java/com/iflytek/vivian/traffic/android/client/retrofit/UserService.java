package com.iflytek.vivian.traffic.android.client.retrofit;

import com.iflytek.vivian.traffic.android.dto.Result;
import com.iflytek.vivian.traffic.android.dto.User;


import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserService {
    @Multipart
    @POST("/traffic-server/user/login")
    Call<Result<User>> userLogin(@Query("username") String username, @Query("password") String password);


}
