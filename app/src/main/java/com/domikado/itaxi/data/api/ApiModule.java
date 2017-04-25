package com.domikado.itaxi.data.api;

import com.domikado.itaxi.BuildConfig;
import com.domikado.itaxi.TaxiApplication;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Modifier;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

@Module
public class ApiModule {

    @Singleton
    @Provides
    Gson provideGson() {
        return new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
            .create();
    }

    @Singleton
    @Provides
    Retrofit provideRetrofit(OkHttpClient okHttpClient, Gson gson) {
        return new Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    }

    @Singleton
    @Provides
    OkHttpClient provideOkHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(Timber::d);
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Interceptor headerInterceptor = chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                .addHeader("Device-Id", TaxiApplication.getComponent().device().getIMEI())
                .addHeader("OS", "Android")
                .addHeader("OS-Version", TaxiApplication.getComponent().device().getOSVersion())
                .addHeader("App-Version", BuildConfig.VERSION_NAME);
            Request request = requestBuilder.build();
            return chain.proceed(request);
        };

        return new OkHttpClient.Builder()
            .addNetworkInterceptor(new StethoInterceptor())
            .addInterceptor(interceptor)
            .addInterceptor(headerInterceptor)
            .build();
    }

    @Singleton
    @Provides
    DomikadoService provideDomikadoService(Retrofit retrofit) {
        return retrofit.create(DomikadoService.class);
    }
}
