package com.iflytek.vivian.traffic.android.client.retrofit;

import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.dto.Result;

import java.security.interfaces.RSAKey;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface EventService {

    @Multipart
    @POST("traffic-server/event/iat")
    Call<Result<Event>> iatEvent(@Part MultipartBody.Part file);

    @POST("/traffic-server/event/save")
    Call<Result<Event>> saveEvent(@Body Event event);

    @POST("/traffic-server/event/delete")
    Call<Result<Boolean>> deleteEvent(@Body List<String> eventIds);

    @POST("/traffic-server/event/update")
    Call<Result<Event>> updateEvent(@Body Event event);

    @POST("/traffic-server/event/list")
    Call<Result<List<Event>>> listEvent();

    @POST("/traffic-server/event/detail")
    Call<Result<Event>> selectEvent(@Query("id") String eventId);

}
