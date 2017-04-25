package com.domikado.itaxi.data.api;

import com.domikado.itaxi.data.api.json.AdsResponseJson;
import com.domikado.itaxi.data.api.json.ApkResponseJson;
import com.domikado.itaxi.data.api.json.ApplicationSettingJson;
import com.domikado.itaxi.data.api.json.NewsResponseJson;
import com.domikado.itaxi.data.api.request.PingRequest;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;
import rx.Observable;

public interface DomikadoService {

    @GET("{id}.json")
    Observable<ApplicationSettingJson> getDefaultSettings(@Path("id") String deviceId);

    @POST("ping")
    Observable<ResponseBody> ping(@Body PingRequest request);

    @GET
    Observable<ApplicationSettingJson> getSettings(@Url String url);

    @GET
    Observable<AdsResponseJson> getAdsPlaylist(@Url String url);

    @GET
    Observable<NewsResponseJson> getNews(@Url String url);

    @GET
    Observable<ApkResponseJson> checkApk(@Url String url);

    @POST
    Observable<ResponseBody> post(@Url String url, @Body RequestBody body);

}
