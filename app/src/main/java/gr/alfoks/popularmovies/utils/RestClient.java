package gr.alfoks.popularmovies.utils;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import android.support.annotation.NonNull;

public final class RestClient<RestApi> {
    private final String baseUrl;
    private final Class<RestApi> restApiClass;
    private final HashMap<String, String> parameters;
    private final HashMap<String, String> headers;

    public RestClient(
        String baseUrl,
        Class<RestApi> restApiClass,
        HashMap<String, String> parameters,
        HashMap<String, String> headers) {
        this.baseUrl = baseUrl;
        this.restApiClass = restApiClass;
        this.parameters = parameters != null ? new HashMap<>(parameters) : new HashMap<String, String>();
        this.headers = headers != null ? new HashMap<>(headers) : new HashMap<String, String>();
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
                    Request request = chain.request();
                    HttpUrl originalUrl = request.url();
                    HttpUrl newUrl = addQueryParameters(originalUrl).build();

                    Request.Builder requestBuilder = request
                        .newBuilder()
                        .url(newUrl);

                    addHeaders(requestBuilder);

                    return chain.proceed(requestBuilder.build());
                }
            })
            .build();
    }

    private HttpUrl.Builder addQueryParameters(HttpUrl url) {
        HttpUrl.Builder urlBuilder = url.newBuilder();

        for(String key : parameters.keySet()) {
            urlBuilder.addQueryParameter(key, parameters.get(key));
        }

        return urlBuilder;
    }

    private void addHeaders(Request.Builder requestBuilder) {
        for(String header : headers.keySet()) {
            requestBuilder.addHeader(header, headers.get(header));
        }
    }
}
