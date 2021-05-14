package com.iflytek.vivian.traffic.android.client.retrofit;

import com.iflytek.vivian.traffic.android.dto.Result;
import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.utils.StringUtil;


import java.security.interfaces.RSAKey;
import java.time.format.ResolverStyle;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {

    @POST("/traffic-server/user/login")
    Call<Result<User>> userLogin(@Body User user);

    @POST("/traffic-server/user/save")
    Call<Result<User>> saveUser(@Body User user);

    @POST("/traffic-server/user/delete")
    Call<Result<Boolean>> deleteUser(@Body List<String> userIds);

    @POST("/traffic-server/user/update")
    Call<Result<User>> updateUser(@Body User user);

    @GET("/traffic-server/user/list")
    Call<Result<List<User>>> listUser();

    @POST("/traffic-server/user/detail/{userId}")
    Call<Result<User>> selectUser(@Path("userId") String userId);

    @Multipart
    @POST("/traffic-server/user/{userId}/image/upload")
    Call<Result<String>> uploadImage(@Part MultipartBody.Part image, @Path("userId") String userId);

    @POST("/traffic-server/user/{userId}/image")
    Call<Result<String>> getUserImage(@Path("userId") String userId);

    @POST("/traffic-server/user/image/update")
    Call<Result<User>> changeUserImage(@Body User user);


    @GET("/traffic-server/user/list/name/asc")
    Call<Result<List<User>>> listUserByNameAsc();

    @GET("/traffic-server/user/list/name/desc")
    Call<Result<List<User>>> listUserByNameDesc();

    @GET("/traffic-server/user/list/id/asc")
    Call<Result<List<User>>> listUserByIdAsc();

    @GET("/traffic-server/user/list/id/desc")
    Call<Result<List<User>>> listUserByIdDesc();

    @GET("/traffic-server/user/list/age/asc")
    Call<Result<List<User>>> listUserByAgeAsc();

    @GET("/traffic-server/user/list/age/desc")
    Call<Result<List<User>>> listUserByAgeDesc();

}
