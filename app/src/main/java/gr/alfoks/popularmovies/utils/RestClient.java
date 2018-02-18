package gr.alfoks.popularmovies.utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import android.support.annotation.NonNull;

public class RestClient<RestApi> {
    private final String baseUrl;
    private Class<RestApi> restApiClass;

    public RestClient(String baseUrl, Class<RestApi> restApiClass) {
        this.baseUrl = baseUrl;
        this.restApiClass = restApiClass;
    }

    public RestApi create() {
        return createRetrofit().create(restApiClass);
    }

    private Retrofit createRetrofit() {
        return new Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
    }

    private OkHttpClient createHttpClient() {
        return new OkHttpClient.Builder()
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(@NonNull Chain chain) throws IOException {
                    Request.Builder ongoing = chain.request().newBuilder();
                    //Add headers if needed
                    return chain.proceed(ongoing.build());
                }
            })
            .build();
    }
}
