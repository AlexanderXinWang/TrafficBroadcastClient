package com.iflytek.vivian.traffic.android.client.retrofit;

import com.iflytek.vivian.traffic.android.dto.Result;
import com.iflytek.vivian.traffic.android.dto.Search;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SearchService {
    @POST("/traffic-server/search/{query}")
    Call<Result<Search>> search(@Path("query") String query);
}
