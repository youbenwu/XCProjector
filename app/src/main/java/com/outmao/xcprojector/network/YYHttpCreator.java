package com.outmao.xcprojector.network;

import com.outmao.xcprojector.BuildConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by lgs on 16/12/20.
 */

public class YYHttpCreator {

    private static final String kBaseRootDIR = "api/";

    public static final String kBseURL = (BuildConfig.HTTP_HOST + kBaseRootDIR);


    private static Retrofit retrofit;

    private static final int maxCacheSize = 1024 * 1024 * 100;
    private static final long connectionTitmeOut = 15 * 1000;
    private static final long writeTimeOut = 15 * 1000;
    private static final int maxTheadCunt = 20;

    public static OkHttpClient getClient() {
        Dispatcher dispatcher = new Dispatcher(Executors.newFixedThreadPool(maxTheadCunt));
        dispatcher.setMaxRequests(maxTheadCunt);
        dispatcher.setMaxRequestsPerHost(1);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.dispatcher(dispatcher)
                .connectTimeout(connectionTitmeOut, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS);

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(logInterceptor);

        builder.addInterceptor(new CommonParamsInterceptor() {
            @Override
            public Map<String, String> getHeaderMap() {  // Header
                Map<String, String> headersMap = new HashMap<>();
                return headersMap;
            }

            @Override
            public Map<String, String> getQueryParamMap(Map<String, String> queryParamMap) { // URL Query
                if(queryParamMap ==null){
                    queryParamMap = new HashMap<>();
                }
                return queryParamMap;
            }

            @Override
            public Map<String, String> getFormBodyParamMap(Map<String, String> formBodyParamMap) { // POST body
                if(formBodyParamMap ==null){
                    formBodyParamMap = new HashMap<>();
                }
                return formBodyParamMap;
            }
        });

        return builder.build();
    }

    private static Retrofit getRetrofit(String baseURL) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(getClient())
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit;
    }

    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = getRetrofit(kBseURL);
        }
        return retrofit;
    }

    public static <T> T createService(Class<T> serviceClass) {
        return getRetrofit().create(serviceClass);
    }
}
