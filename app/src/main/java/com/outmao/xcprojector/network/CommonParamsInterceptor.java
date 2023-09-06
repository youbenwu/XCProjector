package com.outmao.xcprojector.network;

/**
 * Created by Administrator on 2017/6/2.
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 公共参数
 * 1) Header
 * 2) Query Param
 * 3) POST Param form-data
 * 4) POST Param x-www-form-urlencoded
 */
public abstract class CommonParamsInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();
        Request.Builder newRequest = originRequest.newBuilder();

        // Header
        Headers.Builder newHeaderBuilder = originRequest.headers().newBuilder();
        Map<String, String> headerMap = getHeaderMap();
        if (headerMap != null && !headerMap.isEmpty()) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                newHeaderBuilder.add(entry.getKey(), entry.getValue());
            }
            newRequest.headers(newHeaderBuilder.build());
        }

        // Query Param
        if ("GET".equals(originRequest.method())) {

            getQueryParamMap(originRequest, newRequest);
        } else if ("POST".equals(originRequest.method())) {

            String contentType = "";
            try {
                contentType = originRequest.body().contentType().toString();
            } catch (Exception e) {
            }
            //判断请求头是否是使用JSON方式提交，如果是，将通用参数放到URL提交
            if ("application/json".equals(contentType)) {
                getQueryParamMap(originRequest, newRequest);
            } else {
                RequestBody body = originRequest.body();
                if (body != null && body instanceof FormBody) {
                    // POST Param x-www-form-urlencoded
                    FormBody formBody = (FormBody) body;
                    Map<String, String> formBodyParamMap = new HashMap<>();
                    int bodySize = formBody.size();
                    for (int i = 0; i < bodySize; i++) {
                        formBodyParamMap.put(formBody.name(i), formBody.value(i));
                    }

                    getFormBodyParamMap(formBodyParamMap);
                    FormBody.Builder bodyBuilder = new FormBody.Builder();
                    for (Map.Entry<String, String> entry : formBodyParamMap.entrySet()) {
                        bodyBuilder.add(entry.getKey(), entry.getValue());
                    }
                    newRequest.method(originRequest.method(), bodyBuilder.build());
                } else if (body != null && body instanceof MultipartBody) {
                    // POST Param form-data
                    MultipartBody multipartBody = (MultipartBody) body;
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                    Map<String, String> extraFormBodyParamMap = getFormBodyParamMap(new HashMap<String, String>());
                    for (Map.Entry<String, String> entry : extraFormBodyParamMap.entrySet()) {
                        builder.addFormDataPart(entry.getKey(), entry.getValue());
                    }
                    List<MultipartBody.Part> parts = multipartBody.parts();
                    for (MultipartBody.Part part : parts) {
                        builder.addPart(part);
                    }
                    newRequest.post(builder.build());
                } else {
                    Map<String, String> formBodyParamMap = new HashMap<>();
                    getFormBodyParamMap(formBodyParamMap);
                    FormBody.Builder bodyBuilder = new FormBody.Builder();
                    for (Map.Entry<String, String> entry : formBodyParamMap.entrySet()) {
                        bodyBuilder.add(entry.getKey(), entry.getValue());
                    }
                    newRequest.method(originRequest.method(), bodyBuilder.build());
                }

            }


        }

        Response response = chain.proceed(newRequest.build());
        return response;
    }

    private void getQueryParamMap(Request originRequest, Request.Builder newRequest) {
        Set<String> originKey = originRequest.url().queryParameterNames();
        Map<String, String> queryParamMap = new HashMap<>();
        if (!originKey.isEmpty()) {
            for (String key : originKey) {
                queryParamMap.put(key, originRequest.url().queryParameter(key));
            }
        }
        HttpUrl.Builder newUrlBuilder = originRequest.url().newBuilder();
        queryParamMap = getQueryParamMap(queryParamMap);
        if (queryParamMap != null && !queryParamMap.isEmpty()) {
            for (Map.Entry<String, String> entry : queryParamMap.entrySet()) {
                newUrlBuilder.setQueryParameter(entry.getKey(), entry.getValue());
            }

        }

        newRequest.url(newUrlBuilder.build());
    }


    public abstract Map<String, String> getHeaderMap();

    public abstract Map<String, String> getQueryParamMap(Map<String, String> queryParamMap);

    public abstract Map<String, String> getFormBodyParamMap(Map<String, String> queryParamMap);
}
