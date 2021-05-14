package com.iflytek.vivian.traffic.android.client.retrofit;

import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.dto.Result;

import java.security.interfaces.RSAKey;
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

    @GET("/traffic-server/event/list")
    Call<Result<List<Event>>> listEvent();

    @POST("/traffic-server/event/detail/{eventId}")
    Call<Result<Event>> selectEvent(@Path("eventId") String eventId);

    @GET("/traffic-server/event/play")
    Call<Result<List<String>>> getEventPlayPath();

    @POST("/traffic-server/event/list/{userId}")
    Call<Result<List<Event>>> findEventByUserId(@Path("userId") String userId);

    @GET("/traffic-server/event/list/time/asc")
    Call<Result<List<Event>>> listEventByTimeAsc();

    @GET("/traffic-server/event/list/time/desc")
    Call<Result<List<Event>>> listEventByTimeDesc();

    @GET("/traffic-server/event/list/event/asc")
    Call<Result<List<Event>>> listEventByEventAsc();

    @GET("/traffic-server/event/list/event/desc")
    Call<Result<List<Event>>> listEventByEventDesc();

    @GET("/traffic-server/event/list/location/asc")
    Call<Result<List<Event>>> listEventByLocationAsc();

    @GET("/traffic-server/event/list/location/desc")
    Call<Result<List<Event>>> listEventByLocationDesc();

    @GET("/traffic-server/event/list/name/asc")
    Call<Result<List<Event>>> listEventByNameAsc();

    @GET("/traffic-server/event/list/name/desc")
    Call<Result<List<Event>>> listEventByNameDesc();
}
