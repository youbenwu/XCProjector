package com.outmao.xcprojector.api;


import com.outmao.xcprojector.api.models.UserInfo;
import com.outmao.xcprojector.network.ObservableHelper;
import com.outmao.xcprojector.network.YYHttpCreator;
import com.outmao.xcprojector.network.YYResponseData;

import rx.Observable;

public class HttpApiService {

    private static HttpApiService httpApiService;

    public synchronized static HttpApiService getInstance() {
        if (httpApiService == null) {
            httpApiService = new HttpApiService();
        }
        return httpApiService;
    }

    public Observable<YYResponseData<UserInfo>> login(String account, String pwd, String imei) {
        HttpApiServiceInterface api = YYHttpCreator.createService(HttpApiServiceInterface.class);
        Observable<YYResponseData<UserInfo>> result = api.login(account, pwd, imei);
        return ObservableHelper.subscribeOn(result);
    }




}
